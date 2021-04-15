/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.server.core.ui.common.ComponentHorizontalAlignment
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.zk.ui.components.ZkUiComponent
import com.gridnine.jasmine.server.zk.ui.components.configureDimensions
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zul.*
import org.zkoss.zul.Label
import org.zkoss.zul.event.ListDataEvent
import org.zkoss.zul.event.PagingEvent
import org.zkoss.zul.event.ZulEvents
import org.zkoss.zul.ext.Sortable
import java.util.*


class ZkDataGrid<E : Any>(configure: DataGridConfiguration.() -> Unit) : DataGrid<E>, ZkUiComponent {

    private var initialized = false
    private lateinit var component: Grid

    private lateinit var model: GridTableModel<E>

    private lateinit var loader: (DataGridRequest) -> DataGridResponse<E>

    private lateinit var formatter: (item: E, fieldId: String) -> String?

    private var doubleClickListener: ((item: E) -> Unit)? = null

    private val configuration = DataGridConfiguration()

    init {
        configuration.configure()
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if (!initialized) {
            component = Grid()
            component.mold = "paging"
            component.isAutopaging = true
            component.isSpan = configuration.span
            configureDimensions(component, configuration)
            val columns = Columns()
            columns.parent = component
            if (configuration.selectable) {
                val column = Column()
                column.label = ""
                column.width = "20px"
                column.parent = columns
            }
            configuration.columns.forEach {
                val column = Column()
                if (it.fieldId == configuration.initSortingColumn) {
                    column.sortDirection = if (configuration.initSortingOrderAsc) "ascending" else "descending"
                }
                column.label = it.title
                column.width = it.width
                if (it.sortable) {
                    val sorter = GridComparatorWrapper<E>(it.fieldId)
                    column.sortAscending = sorter
                    column.sortDescending = sorter
                }
                column.parent = columns
            }
            model = GridTableModel({ this.loader.invoke(it) },  { component.paginal.pageSize }, configuration.initSortingColumn, configuration.initSortingOrderAsc)
            component.setModel(model)
            component.setRowRenderer(TableRowRenderer(configuration.columns, configuration.selectable,
                    { this.doubleClickListener?.invoke(it) }, { item: E, fieldId -> this.formatter.invoke(item, fieldId) }))
            component.addEventListener(ZulEvents.ON_PAGING, PagingEventListener(model))
            component.addEventListener("onPagingInitRender"
            ) { event -> event.stopPropagation() }
            model.updateData()
            initialized = true
        }
        return component
    }

    override fun updateData() {
        model.updateData()
    }

    override fun getSelected(): List<E> {
        if (!configuration.selectable) {
            return emptyList()
        }
        val result = arrayListOf<E>()
        component.rows.getChildren<Row>().forEach {
            val children = it.getChildren<Checkbox>()
            if(children.isNotEmpty() && children[0] is Checkbox) {
                val checkbox = children[0]
                if (checkbox.isChecked) {
                    result.add(it.getValue())
                }
            }
        }
        return result
    }

    override fun setLoader(loader: (DataGridRequest) -> DataGridResponse<E>) {
        this.loader = loader
    }

    override fun setFormatter(formatter: (item: E, fieldId: String) -> String?) {
        this.formatter = formatter
    }


    override fun setDoubleClickListener(listener: ((item: E) -> Unit)?) {
        this.doubleClickListener = listener
    }

}

internal class PagingEventListener<E : Any>(
        private val tableModel: GridTableModel<E>) : EventListener<PagingEvent> {
    override fun onEvent(event: PagingEvent) {
        event.stopPropagation()
        tableModel.updateData()
    }
}

internal class TableRowRenderer<E : Any>(private val columns: List<DataGridColumnConfiguration>, private val selectable: Boolean,
                                         private val dblClickListener: ((E) -> Unit)?, private val renderer: (row: E, fieldId: String) -> String?) : RowRenderer<E> {

    override fun render(row: Row, data: E, index: Int) {
        if (dblClickListener != null) {
            if (!row.getEventListeners(Events.ON_DOUBLE_CLICK).iterator().hasNext()) {
                row.addEventListener(Events.ON_DOUBLE_CLICK) { event ->
                    if (event is MouseEvent) {
                        dblClickListener.invoke(data)
                    }
                }
            }
        }
        if (selectable) {
            val selectedBox = Checkbox()
            selectedBox.parent = row
        }
        for (column in columns) {
            val labelStr = renderer.invoke(data, column.fieldId)?:""
            val div = Div()
            div.hflex = "1"
            val label = Label(labelStr)
            div.style = when (column.horizontalAlignment) {
                        ComponentHorizontalAlignment.RIGHT -> "text-align: right"
                ComponentHorizontalAlignment.CENTER -> "text-align: center"
                        else -> "text-align: left"
                    }
            label.parent = div
            div.parent = row
        }
        row.setValue(data)
    }
}

internal class GridTableModel<E : Any>(private val loader: (DataGridRequest) -> DataGridResponse<E>,
                                       private val limitProvider: () -> Int, initSortingColumn:String?, initSortingOrderAsc:Boolean?) : AbstractListModel<E>(), Sortable<E> {
    private var _data  = hashMapOf<Int, E>()
    private var _size:Int? = null
    private var _sortingComparator: Comparator<E>? = initSortingColumn?.let{ GridComparatorWrapper(it) }
    private var _ascending = initSortingOrderAsc?:true

    override fun getElementAt(index: Int): E? {
        val item = _data[index]
        if(item == null){
            fillData(index)
        }
        return _data[index]!!
    }

    private fun fillData(idx:Int){
        var sortingColumn:String? = null
        if(_sortingComparator is GridComparatorWrapper<*>){
            sortingColumn = (_sortingComparator as GridComparatorWrapper<*>).propertyName
        }
        val limit = limitProvider.invoke()
        val request = DataGridRequest(idx, limit, !_ascending, sortingColumn)
        val response =loader.invoke(request)
        _size = response.count
        response.data.withIndex().forEach {
            _data[idx+it.index] = it.value
        }
    }
    fun updateData() {
        _data.clear()
        _size = null
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1)
    }

    override fun sort(cmpr: Comparator<E>, ascending: Boolean) {
        if(!Objects.equals(cmpr, _sortingComparator) || ascending != _ascending){
            _data.clear()
            _size = null
        }
        _sortingComparator = cmpr
        _ascending = ascending
        updateData()
    }

    override fun getSortDirection(cmpr: Comparator<E>?): String {
        return if (Objects.equals(_sortingComparator, cmpr)) {
            if (_ascending) "ascending" else "descending"
        } else "natural"
    }

    override fun getSize(): Int {
        if(_size == null){
            fillData(0)
        }
        return _size!!
    }

}

internal class GridComparatorWrapper<E>(val propertyName: String) : Comparator<E> {

    override fun compare(o1: E, o2: E): Int {
        return 0
    }

}