/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.BaseIndexDescription
import com.gridnine.jasmine.server.core.model.domain.DatabasePropertyType
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.standard.helpers.UiListHelper
import com.gridnine.jasmine.server.standard.model.BaseListFilterValue
import com.gridnine.jasmine.server.standard.model.ListFilter
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.server.standard.model.domain.SortOrderType
import com.gridnine.jasmine.web.server.common.*
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.widgets.ServerUiSearchBoxWidget
import com.gridnine.jasmine.web.server.widgets.ServerUiSearchBoxWidgetConfiguration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ServerUiListHandler : ServerUiMainFrameTabHandler<ListWorkspaceItem> {
    override fun getTabId(obj: ListWorkspaceItem): String {
        return obj.uid
    }

    override fun createTabData(obj: ListWorkspaceItem, callback: ServerUiMainFrameTabCallback): ServerUiMainFrameTabData {
        return ServerUiMainFrameTabData(obj.displayName ?: "", ServerUiMainframeListComponent(obj))
    }

}
interface ServerUiListWrapper<E:BaseIntrospectableObject>{
    fun getSelectedItems():List<E>
}

interface ServerUiListToolButton<E:BaseIntrospectableObject>: ServerUiRegistryItem<ServerUiListToolButton<BaseIntrospectableObject>>, ServerUiHasWeight {
    fun isApplicable(listId:String):Boolean
    fun onClick(value: ServerUiListWrapper<E>)
    fun getDisplayName():String
    override fun getType(): ServerUiRegistryItemType<ServerUiListToolButton<BaseIntrospectableObject>> {
        return TYPE
    }
    companion object{
        val TYPE = ServerUiRegistryItemType<ServerUiListToolButton<BaseIntrospectableObject>>("list-button-handlers")
    }
}

class ServerUiMainframeListComponent(private val item: ListWorkspaceItem) : BaseServerUiNodeWrapper<ServerUiBorderContainer>(), ServerUiEventsSubscriber {
    private val updateCallback:()->Unit
    init {
        val centerContent = ServerUiListDataGridPanel(item)
        val listWrapper = object :ServerUiListWrapper<BaseIntrospectableObject>{
            override fun getSelectedItems(): List<BaseIntrospectableObject> {
                return centerContent.getSelectedItems()
            }
        }
        val listBorder = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration {
            width = "100%"
            height = "100%"
        })
        val buttons = ServerUiRegistry.get().allOf(ServerUiListToolButton.TYPE).filter { it.isApplicable(item.listId!!) }.sortedBy { it.getWeight() }
        val northContent = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration {
            width = "100%"
            buttons.forEach {
                columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            }
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        northContent.addRow()
        buttons.forEach {
            val button = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
                title = it.getDisplayName()
            })
            button.setHandler {
                it.onClick(listWrapper)
            }
            northContent.addCell(ServerUiGridLayoutCell(button))
        }
        northContent.addCell(ServerUiGridLayoutCell(null, 1))
        val searchWidget = ServerUiSearchBoxWidget(ServerUiSearchBoxWidgetConfiguration {
            width = "200px"
        })
        northContent.addCell(ServerUiGridLayoutCell(searchWidget, 1))
        val northRegion = ServerUiBorderContainerRegion {
            collapsible = false
            showSplitLine = false
            showBorder = false
            content = northContent
        }
        listBorder.setNorthRegion(northRegion)

        listBorder.setCenterRegion(ServerUiBorderContainerRegion{
            showSplitLine = false
            showBorder = false
            content = centerContent
        })
        searchWidget.setSearchHandler {
            centerContent.updateData()
        }
        val filtersPanel = ServerUiListFilterPanel(item) {
            centerContent.updateData()
        }
        centerContent.filtersProvider = {filtersPanel.getFiltersValues()}
        centerContent.freeTextProvider = {searchWidget.getValue()}
        listBorder.setEastRegion(ServerUiBorderContainerRegion{
            showBorder = true
            title = "Фильтры"
            collapsible = true
            collapsed = true
            width = "250px"
            content = filtersPanel
        })
        _node = listBorder
        updateCallback = {centerContent.updateData()}
    }

    override fun receiveEvent(event: Any) {
        if(event is ServerUiObjectDeleteEvent){
            if(DomainMetaRegistry.get().indexes.values.filter { it.document == event.objectType}.any { it.id == item.listId}){
                updateCallback.invoke()
            }
        }
    }
}

class ServerUiListFilterPanel(private val we: ListWorkspaceItem, private val applyCallback:()->Unit) : BaseServerUiNodeWrapper<ServerUiBorderContainer>() {

    private val filters = arrayListOf<FilterData>()

    private val domainDescr: BaseIndexDescription =
            DomainMetaRegistry.get().indexes[we.listId] ?: DomainMetaRegistry.get().assets[we.listId]
            ?: throw IllegalArgumentException("no description found for ${we.listId}")

    init {
        _node = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration{
            width = "100%"
            height = "100%"
        })
        _node.setCenterRegion(ServerUiBorderContainerRegion{
            showBorder = false
            showSplitLine = false
            content = createFilters()
        })
        _node.setSouthRegion(ServerUiBorderContainerRegion{
                showBorder = false
                showSplitLine = false
                content = createButtons()
            })
        }

    private fun createFilters(): ServerUiGridLayoutContainer {
        val container = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        we.filters.forEach {
            container.addRow()
            val label = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration{})
            label.setText(domainDescr.properties[it]?.getDisplayName() ?: domainDescr.collections[it]!!.getDisplayName())
            container.addCell(ServerUiGridLayoutCell(label))
            val handler = when (domainDescr.properties[it]?.type) {
                DatabasePropertyType.STRING, DatabasePropertyType.TEXT ->
                    ServerUiStringFilterHandler()
                DatabasePropertyType.BOOLEAN ->
                    ServerUiBooleanFilterHandler()
                DatabasePropertyType.ENUM ->
                    ServerUiEnumValueFilterHandler(domainDescr.properties[it]!!.className!!)
                DatabasePropertyType.LOCAL_DATE ->
                    ServerUiDateFilterHandler()
                DatabasePropertyType.LOCAL_DATE_TIME ->
                    ServerUiDateTimeFilterHandler()
                DatabasePropertyType.BIG_DECIMAL ->
                    ServerUiBigDecimalFilterHandler()
                DatabasePropertyType.ENTITY_REFERENCE ->
                    ServerUiEntityValuesFilterHandler(domainDescr.properties[it]!!.className!!)
                else -> null
            }
            val noPadding =  when (domainDescr.properties[it]?.type) {
                DatabasePropertyType.LOCAL_DATE ,DatabasePropertyType.LOCAL_DATE_TIME,DatabasePropertyType.BIG_DECIMAL -> true
                else -> false
            }
            if (handler != null) {
                val component = handler.createEditor()
                container.addRow()
                container.addCell(ServerUiGridLayoutCell(component, sClass = if(noPadding) "jasmine-grid-container-no-padding" else null))
                filters.add(FilterData(it, component, handler as ServerUiListFilterHandler<BaseListFilterValue, ServerUiNode>))
            }
        }
        return container
    }

    private fun createButtons(): ServerUiGridLayoutContainer {
        val container = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("50%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("50%"))
        })
        container.addRow()
        val applyButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            width = "100%"
            title = "Применить"
        })
        applyButton.setHandler {
            applyCallback.invoke()
        }
        container.addCell(ServerUiGridLayoutCell(applyButton))
        val resetButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            width = "100%"
            title = "Сбросить"
        })
        resetButton.setHandler {
            filters.forEach { it.handler.reset(it.comp) }
            applyCallback.invoke()
        }
        container.addCell(ServerUiGridLayoutCell(resetButton))
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


internal class FilterData(val fieldId: String, val comp: ServerUiNode, val handler: ServerUiListFilterHandler<BaseListFilterValue, ServerUiNode>)

internal class ServerUiListDataGridPanel(val we: ListWorkspaceItem, ) : BaseServerUiNodeWrapper<ServerUiDataGridComponent<BaseIntrospectableObject>>() {
    lateinit var freeTextProvider:()->String?
    lateinit var filtersProvider:()->List<ListFilter>

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    init {
        val indexDescription = DomainMetaRegistry.get().indexes[we.listId] ?: DomainMetaRegistry.get().assets[we.listId]
        ?: error("unable to find description for list ${we.listId}")
        _node = ServerUiLibraryAdapter.get().createDataGrid(ServerUiDataGridComponentConfiguration {
            height = "100%"
            width = "100%"
            selectable = true
            span = true
            columns.addAll(we.columns.map { column ->
                val pd = indexDescription.properties[column]
                if (pd != null) {
                    ServerUiDataGridColumnConfiguration {
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
                            DatabasePropertyType.ENTITY_REFERENCE -> ServerUiComponentHorizontalAlignment.LEFT
                            DatabasePropertyType.LONG,
                            DatabasePropertyType.INT,
                            DatabasePropertyType.BIG_DECIMAL -> ServerUiComponentHorizontalAlignment.RIGHT
                        }
                        width = "150px"
                    }
                } else {
                    val cd = indexDescription.collections[column]
                            ?: error("neither property nor column with id ${column} found in list ${we.listId}")
                    ServerUiDataGridColumnConfiguration {
                        title = cd.getDisplayName()!!
                        fieldId = column
                        sortable = true
                        horizontalAlignment = ServerUiComponentHorizontalAlignment.LEFT
                        width = "200px"
                    }
                }

            })
            if (we.sortOrders.isNotEmpty()) {
                val sorting = we.sortOrders[0]
                initSortingColumn = sorting.field
                initSortingOrderAsc = sorting.orderType == SortOrderType.ASC
            }
        })
        _node.setFormatter { item, fieldId ->
            val value = if (indexDescription.properties.containsKey(fieldId)) item.getValue(fieldId) else item.getCollection(fieldId)
            lateinit var displayName: String
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
                ServerUiMainFrame.get().openTab(it.document!!, it.uid)
            }
        }
        _node.setLoader {request ->
                val searchResult = UiListHelper.search(listId = we.listId!!, criterions = we.criterions, filters = filtersProvider.invoke(), columns = we.columns,
                        freeText = freeTextProvider.invoke(),  offset = request.offSet, limit = request.limit, sortColumn = request.sortColumn, sortDesc = request.desc == true)
            ServerUiDataGridResponse(searchResult.first, searchResult.second)
        }
    }

    internal fun updateData(){
        _node.updateData()
    }

    fun getSelectedItems(): List<BaseIntrospectableObject> {
        return _node.getSelected()
    }

}