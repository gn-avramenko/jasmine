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

class AntdWebDataGrid<E:BaseIntrospectableObjectJS>(configure:WebDataGridConfiguration<E>.()->Unit) :WebDataGrid<E>,BaseAntdWebUiComponent(){

    private val config = WebDataGridConfiguration<E>()
    private val columns:Array<dynamic>

    private var currentPage = 1
    private var pageSizeValue = 50
    private var totalItemsCount = 0L

    private var sortingColumn:String? = null
    private var sortAsc = true

    private val currentPageData = arrayListOf<E>()

    private val localData = arrayListOf<E>()

    private val selectedKeys = arrayListOf<Int>()

    private var selectionChangeListener: (() -> Unit)? = null

    private var clickListener: (suspend (E) -> Unit)? = null

    init {
        config.configure()
        columns = config.columns.map {conf ->
            val res = object{
                val title = conf.title
                val dataIndex = conf.fieldId
                val sorter = conf.sortable
                val render = { _:dynamic, record:E, index:Int ->
                    if(conf.formatter != null){
                        ReactFacade.createElementWithChildren(ReactFacade.Fragment, object{}, conf.formatter!!.invoke(record.getValue(conf.fieldId), record, index)?:"")
                    } else {
                        ReactFacade.createElementWithChildren(ReactFacade.Fragment, object{},record.getValue(conf.fieldId).toString())
                    }
                }
                val align = when(conf.horizontalAlignment){
                    WebDataHorizontalAlignment.LEFT -> "left"
                    WebDataHorizontalAlignment.RIGHT -> "right"
                    WebDataHorizontalAlignment.CENTER -> "center"
                    null -> "center"
                }
            }.asDynamic()
            if(conf.width != null && !config.fitColumns){
                res.width = conf.width
            }
            res
        }.toTypedArray()
    }

    private var loader: (suspend (WebDataGridRequest) -> WebDataGridResponse<E>)? = null

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxyAdvanced({
             val props = object {}.asDynamic()
             props.style = object {}.asDynamic()
             if(config.fit){
                 props.style.width  = "100%"
                 props.style.height  = "100%"
             } else{
                 if(config.width != null){
                     props.style.width  = config.width
                 }
                 if(config.height != null){
                     props.style.height  = config.height
                 }
             }
             if(!config.showPagination){
                props.pagination = false
             } else{
                 props.pagination = object {
                     val current = currentPage
                     val pageSize = pageSizeValue
                     val total = totalItemsCount
                 }
             }
             props.columns = columns
             props.dataSource = currentPageData.toTypedArray()
             props.onChange = { pagination:dynamic, filters:dynamic, sorter:dynamic ->
                 pageSizeValue = pagination.pageSize
                 currentPage = pagination.current
                 sortingColumn = sorter.field
                 sortAsc = sorter.order =="ascend"
                 selectedKeys.clear()
                 reload()
             }
            props.scroll = object{
                val  x = "max-content"
                val  y = "calc(100vh - 200px)"
            }
            props.rowKey = { record:dynamic ->
                currentPageData.indexOf(record).toString()
            }
            when(config.selectionType){
                DataGridSelectionType.SINGLE ->{
                    props.rowSelection = object{
                        val type = "radio"
                        val onChange = {selectedRowKeys:Array<String>, selectedRows:dynamic ->
                            selectedKeys.clear()
                            selectedKeys.addAll(selectedRowKeys.map { it.toInt() })
                            selectionChangeListener?.invoke()
                        }
                    }
                }
                DataGridSelectionType.MULTIPLE ->{
                    props.rowSelection = object{
                        val type = "checkbox"
                        val onChange = {selectedRowKeys:Array<String>, selectedRows:dynamic ->
                            selectedKeys.clear()
                            selectedKeys.addAll(selectedRowKeys.map { it.toInt() })
                        }
                    }
                }
            }
            props.onRow={ record:dynamic, rowIndex:Int ->
                object {
                    val onDoubleClick = { event:dynamic ->
                        console.log(event)
                    }
                };
            }
             ReactFacade.createElement(ReactFacade.Table, props)
        }, object {
            val componentDidMount = {
                window.setTimeout({
                    reload()
                }, 100)
            }
        })
    }

    override fun setLoader(loader: suspend (WebDataGridRequest) -> WebDataGridResponse<E>) {
        this.loader = loader
    }

    override fun setLocalData(data: List<E>) {
        localData.clear()
        localData.addAll(data)
        if(isInitialized()){
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
        if(config.dataType == DataGridDataType.LOCAL){
            if(sortingColumn != null){
                val formatter = config.columns.find { it.fieldId == sortingColumn }?.formatter?:defaultFormatter
                if(sortAsc){
                    localData.sortBy { formatter.invoke(it.getValue(sortingColumn!!), it, 0 ) }
                } else {
                    localData.sortByDescending { formatter.invoke(it.getValue(sortingColumn!!), it, 0 ) }
                }
            }
            totalItemsCount = localData.size.toLong()
            var startIndex = pageSizeValue*(currentPage-1)
            if(startIndex>= localData.size){
                startIndex = 0
                currentPage = 1
            }
            var endIndex = startIndex+pageSizeValue
            if(endIndex >= localData.size){
                endIndex = localData.size
            }
            val sublist = localData.subList(startIndex, endIndex)
            currentPageData.clear()
            currentPageData.addAll(sublist)
        } else {
            launch {
                var remoteData = loader!!.invoke(WebDataGridRequest().also {
                    it.desc = !sortAsc
                    it.sortColumn = sortingColumn
                    it.page = currentPage-1
                    it.rows = pageSizeValue
                })
                totalItemsCount = remoteData.count
                if((currentPage-1)*pageSizeValue > totalItemsCount){
                    currentPage = 1
                    remoteData = loader!!.invoke(WebDataGridRequest().also {
                        it.desc = !sortAsc
                        it.sortColumn = sortingColumn
                        it.page = currentPage-1
                        it.rows = pageSizeValue
                    })
                }
                currentPageData.clear()
                currentPageData.addAll(remoteData.data)
            }
        }
        maybeRedraw()
    }
    private val defaultFormatter = {value: Any?, row: E, index: Int ->
        value.toString()
    }
}