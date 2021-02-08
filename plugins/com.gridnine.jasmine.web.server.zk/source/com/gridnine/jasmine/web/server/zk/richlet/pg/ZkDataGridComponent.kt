/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.AbstractComponent
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zul.*
import org.zkoss.zul.event.ListDataEvent
import org.zkoss.zul.event.PagingEvent
import org.zkoss.zul.event.ZulEvents
import org.zkoss.zul.ext.Sortable
import java.util.*


class ZkDataGridComponent<E : Any>(private val configuration: DataGridComponentConfiguration<E>) :DataGridComponent<E>{

    private var initialized = false
    private lateinit var component:Grid

    private lateinit var model:GridTableModel<E>

    fun getComponent():AbstractComponent{
        if(!initialized) {
            component = Grid()
            component.mold = "paging"
            component.isAutopaging = true
            component.vflex = configuration.vFlex
            component.hflex = configuration.hFlex
            val columns = Columns()
            columns.parent = component
            if (configuration.selectable) {
                val column = Column()
                column.label = ""
                column.parent = columns
            }
            configuration.columns.forEach {
                val column = Column()
                if (it.first == configuration.initSortingColumn) {
                    column.sortDirection = if (configuration.initSortingOrderAsc) "ascending" else "descending"
                }
                column.label = it.second
                if (it.third) {
                    val sorter = GridComparatorWrapper<E>(it.first)
                    column.sortAscending = sorter
                    column.sortDescending = sorter
                }
                column.parent = columns
            }
            model = GridTableModel(configuration.loader, { component.paginal.activePage }, { component.paginal.totalSize })
            component.setModel(model)
            component.setRowRenderer(TableRowRenderer(configuration.columns.map { it.first }, configuration.selectable,
                    configuration.dblClickListener, configuration.renderer))
            component.addEventListener(ZulEvents.ON_PAGING, PagingEventListener(model))
            initialized = true
        }
        return component
    }

    override fun updateData() {
        model.updateData()
    }

    override fun getSelected(): List<E> {
        if(!configuration.selectable){
            return emptyList()
        }
        val result = arrayListOf<E>()
        component.rows.groups[0].items.forEach {
            val checkbox = it.firstChild as Checkbox
            if(checkbox.isChecked){
                result.add(it.getValue())
            }
        }
        return result
    }

}

internal class PagingEventListener<E : Any>(
        private val tableModel: GridTableModel<E>) : EventListener<PagingEvent> {
    override fun onEvent(event: PagingEvent) {
        event.stopPropagation()
        tableModel.updateData()
    }
}

internal class TableRowRenderer<E : Any>(private val fieldsIds: List<String>, private val selectable: Boolean,
                                         private val dblClickListener: ((E) -> Unit)?, private val renderer: (row: E, fieldId: String) -> String?) : RowRenderer<E> {

    override fun render(row: Row, data: E, index: Int) {
        if (dblClickListener != null) {
            if(!row.getEventListeners(Events.ON_DOUBLE_CLICK).iterator().hasNext()) {
                row.addEventListener(Events.ON_DOUBLE_CLICK) { event ->
                    if (event is MouseEvent) {
                        dblClickListener.invoke(data)
                    }
                }
            }
        }
        if(selectable) {
            val selectedBox=Checkbox()
            selectedBox.parent = row
        }
        for(fieldId in fieldsIds) {
            val str = renderer.invoke(data, fieldId)?:""
            Label(str).parent = row
        }
        row.setValue(data)
    }
}

internal class GridTableModel<E : Any>(private val loader: (DataGridRequest) -> DataGridResponse<E>, private val currentPageProvider: () -> Int,
                                       private val limitProvider: () -> Int) : AbstractListModel<E>(), Sortable<E> {
    private var _data: List<E> = emptyList()
    private var _offset = 0
    private var _size = 0
    private var _sortingComparator: Comparator<E>? = null
    private var _ascending = false

    override fun getElementAt(index: Int): E? {
        val idx = index - _offset
        return if (idx < 0 || idx >= _data.size) null else _data[idx]
    }

    fun updateData() {
        _offset = currentPageProvider.invoke() * limitProvider.invoke()
        var sortingColumn:String? = null
        if(_sortingComparator is GridComparatorWrapper<*>){
            sortingColumn = (_sortingComparator as GridComparatorWrapper<*>).propertyName
        }
        val result = loader.invoke(DataGridRequest(currentPageProvider.invoke(), limitProvider.invoke(), _ascending, sortingColumn))
        _size = result.count
        _data  = result.data
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1)
    }

    override fun sort(cmpr: Comparator<E>, ascending: Boolean) {
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
        return _size
    }

}
internal class GridComparatorWrapper<E>(val propertyName: String) : Comparator<E> {

    override fun compare(o1: E, o2: E): Int {
        return 0
    }

}