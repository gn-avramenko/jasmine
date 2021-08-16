/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import kotlinx.browser.document
import kotlinx.browser.window

class AntdWebDataGrid<E : BaseIntrospectableObjectJS>(configure: WebDataGridConfiguration<E>.() -> Unit) :
    WebDataGrid<E>, BaseAntdWebUiComponent() {

    private val config = WebDataGridConfiguration<E>()
    private val columns: Array<dynamic>

    private var currentPage = 1
    private var pageSizeValue = 50
    private var totalItemsCount = 0L

    private var sortingColumn: String? = null
    private var sortAsc = true

    private val currentPageData = arrayListOf<E>()

    private val localData = arrayListOf<E>()

    private val selectedKeys = arrayListOf<Int>()

    private var selectionChangeListener: (() -> Unit)? = null

    private var clickListener: (suspend (E) -> Unit)? = null

    init {
        config.configure()
        columns = config.columns.map { conf ->
            val fieldId = conf.fieldId
            val formatter = conf.formatter
            val res = js("{}")
            res.title = conf.title
            res.dataIndex = conf.fieldId
            res.sorter = conf.sortable
            res.render =  { _: dynamic, record: E, index: Int ->
                if (formatter != null) {
                    ReactFacade.createElementWithChildren(
                        ReactFacade.Fragment,
                        object {},
                        formatter.invoke(record.getValue(fieldId), record, index) ?: ""
                    )
                } else {
                    ReactFacade.createElementWithChildren(
                        ReactFacade.Fragment,
                        object {},
                        record.getValue(fieldId).toString()
                    )
                }
            }
            res.align = when (conf.horizontalAlignment) {
                WebDataHorizontalAlignment.LEFT -> "left"
                WebDataHorizontalAlignment.RIGHT -> "right"
                WebDataHorizontalAlignment.CENTER -> "center"
                null -> "center"
            }
            if (conf.width != null && !config.fitColumns) {
                res.width = conf.width
            }
            res
        }.toTypedArray()
    }

    private var loader: (suspend (WebDataGridRequest) -> WebDataGridResponse<E>)? = null

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxyAdvanced(parentIndex, {parentIndexValue:Int?, childIndex:Int ->
            val props = js("{}")
            props.style = js("{}")
            if (config.fit) {
                props.style.width = "100%"
                props.style.height = "100%"
            } else {
                if (config.width != null) {
                    props.style.width = config.width
                }
                if (config.height != null) {
                    props.style.height = config.height
                }
            }
            if (!config.showPagination) {
                props.pagination = false
            } else {
                props.pagination = js("{}")
                props.pagination.current = currentPage
                props.pagination.pageSize = pageSizeValue
                props.pagination.total = totalItemsCount
            }
            props.columns = columns
            props.dataSource = currentPageData.toTypedArray()
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange =
                { pagination: dynamic, filters: dynamic, sorter: dynamic ->
                    pageSizeValue = pagination.pageSize
                    currentPage = pagination.current
                    sortingColumn = sorter.field
                    sortAsc = sorter.order == "ascend"
                    selectedKeys.clear()
                    reload()
                }
            props.onChange = { pagination: dynamic, filters: dynamic, sorter: dynamic ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange(pagination, filters, sorter)
            }
            props.scroll = js("{}")
            props.scroll.x = "max-content"
            props.scroll.y = "calc(100vh - 200px)"

            ReactFacade.getCallbacks(parentIndexValue, childIndex).rowKey = { record: dynamic ->
                currentPageData.indexOf(record).toString()
            }
            props.rowKey = { record: dynamic ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).rowKey(record)
            }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).rowSelectionOnChange =
                { selectedRowKeys: Array<String>, selectedRows: dynamic ->
                    selectedKeys.clear()
                    selectedKeys.addAll(selectedRowKeys.map { it.toInt() })
                    selectionChangeListener?.invoke()
                }
            props.rowSelection = js("{}")
            props.rowSelection.onChange = { selectedRowKeys: Array<String>, selectedRows: dynamic ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).rowSelectionOnChange(selectedRowKeys, selectedRows)
            }
            props.rowSelection.type = when(config.selectionType){
                DataGridSelectionType.NONE -> "radio"
                DataGridSelectionType.SINGLE -> "radio"
                DataGridSelectionType.MULTIPLE -> "checkbox"
            }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onDoubleClick = { index: Int ->
                val value = currentPageData[index]
                clickListener?.let {
                    launch {
                        it.invoke(value)
                    }
                }
            }

            props.onRow = { record: dynamic, rowIndex: Int ->
                val onRowProps = js("{}")
                onRowProps.onDoubleClick = { event: dynamic ->
                    ReactFacade.getCallbacks(parentIndexValue, childIndex).onDoubleClick(rowIndex)
                }
                onRowProps
            }
            ReactFacade.createElement(ReactFacade.Table, props)
        }, object {
            val componentDidMount = {
                reload()
            }
        })
    }

    override fun setLoader(loader: suspend (WebDataGridRequest) -> WebDataGridResponse<E>) {
        this.loader = loader
    }

    override fun setLocalData(data: List<E>) {
        localData.clear()
        localData.addAll(data)
        if (isInitialized()) {
            reload()
        }
    }

    override fun getSelected(): List<E> {
        return selectedKeys.map { currentPageData[it] }
    }

    override fun setSelectionChangeListener(value: () -> Unit) {
        selectionChangeListener = value
    }

    override fun setRowDblClickListener(listener: suspend (E) -> Unit) {
        clickListener = listener
    }

    override fun reload() {
        selectedKeys.clear()
        if (config.dataType == DataGridDataType.LOCAL) {
            if (sortingColumn != null) {
                val formatter = config.columns.find { it.fieldId == sortingColumn }?.formatter ?: defaultFormatter
                if (sortAsc) {
                    localData.sortBy { formatter.invoke(it.getValue(sortingColumn!!), it, 0) }
                } else {
                    localData.sortByDescending { formatter.invoke(it.getValue(sortingColumn!!), it, 0) }
                }
            }
            totalItemsCount = localData.size.toLong()
            var startIndex = pageSizeValue * (currentPage - 1)
            if (startIndex >= localData.size) {
                startIndex = 0
                currentPage = 1
            }
            var endIndex = startIndex + pageSizeValue
            if (endIndex >= localData.size) {
                endIndex = localData.size
            }
            val sublist = localData.subList(startIndex, endIndex)
            currentPageData.clear()
            currentPageData.addAll(sublist)
            window.setTimeout({
                maybeRedraw()
            }, 20)
        } else {
            launch {
                var remoteData = loader!!.invoke(WebDataGridRequest().also {
                    it.desc = !sortAsc
                    it.sortColumn = sortingColumn
                    it.page = currentPage - 1
                    it.rows = pageSizeValue
                })
                totalItemsCount = remoteData.count
                if ((currentPage - 1) * pageSizeValue > totalItemsCount) {
                    currentPage = 1
                    remoteData = loader!!.invoke(WebDataGridRequest().also {
                        it.desc = !sortAsc
                        it.sortColumn = sortingColumn
                        it.page = currentPage - 1
                        it.rows = pageSizeValue
                    })
                }
                currentPageData.clear()
                currentPageData.addAll(remoteData.data)
                maybeRedraw()
            }
        }

    }

    private val defaultFormatter = { value: Any?, row: E, index: Int ->
        value.toString()
    }
}