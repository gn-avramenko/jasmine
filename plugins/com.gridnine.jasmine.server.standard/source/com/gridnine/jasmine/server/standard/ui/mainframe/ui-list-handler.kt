/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.meta.BaseIndexDescription
import com.gridnine.jasmine.common.core.meta.DatabasePropertyType
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseIndex
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.standard.model.BaseListFilterValue
import com.gridnine.jasmine.common.standard.model.ListFilter
import com.gridnine.jasmine.common.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.SortOrderType
import com.gridnine.jasmine.server.core.ui.common.*
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.widgets.SearchBoxWidget
import com.gridnine.jasmine.server.standard.helpers.UiListHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListTabHandler : MainFrameTabHandler<ListWorkspaceItem> {
    override fun getTabId(obj: ListWorkspaceItem): String {
        return obj.uid
    }

    override fun createTabData(obj: ListWorkspaceItem, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(obj.displayName ?: "", MainframeListComponent(obj))
    }

}
interface ListWrapper<E:BaseIntrospectableObject>{
    fun getSelectedItems():List<E>
}

interface ListToolButton<E:BaseIntrospectableObject>: RegistryItem<ListToolButton<BaseIntrospectableObject>>, HasWeight {
    fun isApplicable(listId:String):Boolean
    fun onClick(value: ListWrapper<E>)
    fun getDisplayName():String
    override fun getType(): RegistryItemType<ListToolButton<BaseIntrospectableObject>> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ListToolButton<BaseIntrospectableObject>>("list-button-handlers")
    }
}

class MainframeListComponent(private val item: ListWorkspaceItem) : BaseNodeWrapper<BorderContainer>(), EventsSubscriber {
    private val updateCallback:()->Unit
    init {
        val centerContent = ListDataGridPanel(item)
        val listWrapper = object : ListWrapper<BaseIntrospectableObject> {
            override fun getSelectedItems(): List<BaseIntrospectableObject> {
                return centerContent.getSelectedItems()
            }
        }
        val listBorder = UiLibraryAdapter.get().createBorderLayout {
            width = "100%"
            height = "100%"
        }
        val buttons = Registry.get().allOf(ListToolButton.TYPE).filter { it.isApplicable(item.listId!!) }.sortedBy { it.getWeight() }
        val northContent = UiLibraryAdapter.get().createGridLayoutContainer {
            width = "100%"
            buttons.forEach { _ ->
                columns.add(GridLayoutColumnConfiguration("auto"))
            }
            columns.add(GridLayoutColumnConfiguration("100%"))
            columns.add(GridLayoutColumnConfiguration("auto"))
        }
        northContent.addRow()
        buttons.forEach {
            val button = UiLibraryAdapter.get().createLinkButton{
                title = it.getDisplayName()
            }
            button.setHandler {
                it.onClick(listWrapper)
            }
            northContent.addCell(GridLayoutCell(button))
        }
        northContent.addCell(GridLayoutCell(null, 1))
        val searchWidget = SearchBoxWidget{
            width = "200px"
        }
        northContent.addCell(GridLayoutCell(searchWidget, 1))
        listBorder.setNorthRegion{
            collapsible = false
            showSplitLine = false
            showBorder = false
            content = northContent
        }

        listBorder.setCenterRegion{
            showSplitLine = false
            showBorder = false
            content = centerContent
        }
        searchWidget.setSearchHandler {
            centerContent.updateData()
        }
        val filtersPanel = ListFilterPanel(item) {
            centerContent.updateData()
        }
        centerContent.filtersProvider = {filtersPanel.getFiltersValues()}
        centerContent.freeTextProvider = {searchWidget.getValue()}
        listBorder.setEastRegion{
            showBorder = true
            title = "Фильтры"
            collapsible = true
            collapsed = true
            width = "250px"
            content = filtersPanel
        }
        _node = listBorder
        updateCallback = {centerContent.updateData()}
    }

    override fun receiveEvent(event: Any) {
        if(event is ObjectDeleteEvent){
            if(DomainMetaRegistry.get().indexes.values.filter { it.document == event.objectType}.any { it.id == item.listId}){
                updateCallback.invoke()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ListFilterPanel(private val we: ListWorkspaceItem, private val applyCallback:()->Unit) : BaseNodeWrapper<BorderContainer>() {

    private val filters = arrayListOf<FilterData>()

    private val domainDescr: BaseIndexDescription =
            DomainMetaRegistry.get().indexes[we.listId] ?: DomainMetaRegistry.get().assets[we.listId]
            ?: throw IllegalArgumentException("no description found for ${we.listId}")

    init {
        _node = UiLibraryAdapter.get().createBorderLayout{
            width = "100%"
            height = "100%"
        }
        _node.setCenterRegion{
            showBorder = false
            showSplitLine = false
            content = createFilters()
        }
        _node.setSouthRegion{
                showBorder = false
                showSplitLine = false
                content = createButtons()
            }
        }

    private fun createFilters(): GridLayoutContainer {
        val container = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        we.filters.forEach {
            container.addRow()
            val label = UiLibraryAdapter.get().createLabel { }
            label.setText(domainDescr.properties[it]?.getDisplayName() ?: domainDescr.collections[it]!!.getDisplayName())
            container.addCell(GridLayoutCell(label))
            val handler = when (domainDescr.properties[it]?.type) {
                DatabasePropertyType.STRING, DatabasePropertyType.TEXT ->
                    StringFilterHandler()
                DatabasePropertyType.BOOLEAN ->
                    BooleanFilterHandler()
                DatabasePropertyType.ENUM ->
                    EnumValueFilterHandler(domainDescr.properties[it]!!.className!!)
                DatabasePropertyType.LOCAL_DATE ->
                    DateFilterHandler()
                DatabasePropertyType.LOCAL_DATE_TIME ->
                    DateTimeFilterHandler()
                DatabasePropertyType.BIG_DECIMAL ->
                    BigDecimalFilterHandler()
                DatabasePropertyType.ENTITY_REFERENCE ->
                    EntityValuesFilterHandler(domainDescr.properties[it]!!.className!!)
                else -> null
            }
            val noPadding =  when (domainDescr.properties[it]?.type) {
                DatabasePropertyType.LOCAL_DATE ,DatabasePropertyType.LOCAL_DATE_TIME,DatabasePropertyType.BIG_DECIMAL -> true
                else -> false
            }
            if (handler != null) {
                val component = handler.createEditor()
                container.addRow()
                container.addCell(GridLayoutCell(component, sClass = if(noPadding) "jasmine-grid-container-no-padding" else null))
                filters.add(FilterData(it, component, handler as ListFilterHandler<BaseListFilterValue, UiNode>))
            }
        }
        return container
    }

    private fun createButtons(): GridLayoutContainer {
        val container = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("50%"))
            columns.add(GridLayoutColumnConfiguration("50%"))
        }
        container.addRow()
        val applyButton = UiLibraryAdapter.get().createLinkButton{
            width = "100%"
            title = "Применить"
        }
        applyButton.setHandler {
            applyCallback.invoke()
        }
        container.addCell(GridLayoutCell(applyButton))
        val resetButton = UiLibraryAdapter.get().createLinkButton{
            width = "100%"
            title = "Сбросить"
        }
        resetButton.setHandler {
            filters.forEach { it.handler.reset(it.comp) }
            applyCallback.invoke()
        }
        container.addCell(GridLayoutCell(resetButton))
        return container
    }

    fun getFiltersValues(): List<ListFilter> {
        return filters.filter { it.handler.isNotEmpty(it.comp) }.map {
            val res = ListFilter()
            res.fieldId = it.fieldId
            res.value = it.handler.getValue(it.comp)
            res
        }.toList()
    }
}


internal class FilterData(val fieldId: String, val comp: UiNode, val handler: ListFilterHandler<BaseListFilterValue, UiNode>)

internal class ListDataGridPanel(val we: ListWorkspaceItem) : BaseNodeWrapper<DataGrid<BaseIntrospectableObject>>() {
    lateinit var freeTextProvider:()->String?
    lateinit var filtersProvider:()->List<ListFilter>

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    init {
        val indexDescription = DomainMetaRegistry.get().indexes[we.listId] ?: DomainMetaRegistry.get().assets[we.listId]
        ?: error("unable to find description for list ${we.listId}")
        _node = UiLibraryAdapter.get().createDataGrid {
            height = "100%"
            width = "100%"
            selectable = true
            span = true
            columns.addAll(we.columns.map { column ->
                val pd = indexDescription.properties[column]
                if (pd != null) {
                    DataGridColumnConfiguration {
                        title = pd.getDisplayName()!!
                        fieldId = column
                        sortable = true
                        horizontalAlignment = when (pd.type) {
                            DatabasePropertyType.STRING,
                            DatabasePropertyType.TEXT,
                            DatabasePropertyType.LOCAL_DATE,
                            DatabasePropertyType.LOCAL_DATE_TIME,
                            DatabasePropertyType.ENUM,
                            DatabasePropertyType.BOOLEAN,
                            DatabasePropertyType.ENTITY_REFERENCE -> ComponentHorizontalAlignment.LEFT
                            DatabasePropertyType.LONG,
                            DatabasePropertyType.INT,
                            DatabasePropertyType.BIG_DECIMAL -> ComponentHorizontalAlignment.RIGHT
                        }
                        width = "150px"
                    }
                } else {
                    val cd = indexDescription.collections[column]
                            ?: error("neither property nor column with id $column found in list ${we.listId}")
                    DataGridColumnConfiguration {
                        title = cd.getDisplayName()!!
                        fieldId = column
                        sortable = true
                        horizontalAlignment = ComponentHorizontalAlignment.LEFT
                        width = "200px"
                    }
                }

            })
            if (we.sortOrders.isNotEmpty()) {
                val sorting = we.sortOrders[0]
                initSortingColumn = sorting.field
                initSortingOrderAsc = sorting.orderType == SortOrderType.ASC
            }
        }
        _node.setFormatter { item, fieldId ->
            val value = if (indexDescription.properties.containsKey(fieldId)) item.getValue(fieldId) else item.getCollection(fieldId)
            if (value is Enum<*>) {
                val enumDescr = DomainMetaRegistry.get().enums[value::class.qualifiedName]
                if (enumDescr != null) {
                    enumDescr.items[value.name]!!.getDisplayName()!!
                } else {
                    value.name
                }

            } else if (value is Boolean) {
                if (value) "Да" else "Нет"
            } else if (value is LocalDate) {
                dateFormatter.format(value)
            } else if (value is LocalDateTime) {
                dateTimeFormatter.format(value)
            } else {
                value?.toString() ?: ""
            }
        }
        _node.setDoubleClickListener {
            if(it is BaseIndex<*>){
                MainFrame.get().openTab(it.document!!, it.uid)
            }
        }
        _node.setLoader {request ->
                val searchResult = UiListHelper.search(listId = we.listId!!, criterions = we.criterions, filters = filtersProvider.invoke(), columns = we.columns,
                        freeText = freeTextProvider.invoke(),  offset = request.offSet, limit = request.limit, sortColumn = request.sortColumn, sortDesc = request.desc == true)
            DataGridResponse(searchResult.first, searchResult.second)
        }
    }

    internal fun updateData(){
        _node.updateData()
    }

    fun getSelectedItems(): List<BaseIntrospectableObject> {
        return _node.getSelected()
    }

}