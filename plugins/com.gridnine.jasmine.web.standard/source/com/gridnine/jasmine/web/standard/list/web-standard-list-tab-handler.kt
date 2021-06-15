/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.standard.list

import com.gridnine.jasmine.common.core.meta.BaseIndexDescriptionJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.core.model.BaseIndexJS
import com.gridnine.jasmine.common.standard.model.rest.BaseListFilterValueDTJS
import com.gridnine.jasmine.common.standard.model.rest.GetListRequestJS
import com.gridnine.jasmine.common.standard.model.rest.ListFilterDTJS
import com.gridnine.jasmine.common.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.*
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidgetCell
import com.gridnine.jasmine.web.standard.widgets.WebLabelWidget

class WebListMainFrameTabHandler : MainFrameTabHandler<ListWorkspaceItemDTJS>{
    override fun getTabId(obj: ListWorkspaceItemDTJS): String {
        return obj.uid!!
    }

    override suspend fun createTabData(obj: ListWorkspaceItemDTJS, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(obj.displayName!!, ListPanel(obj,WebActionsHandler.get().getActionsFor(obj.listId!!)))
    }

    override fun getId(): String {
        return ListWorkspaceItemDTJS::class.simpleName!!
    }

}

class ListPanel(we: ListWorkspaceItemDTJS, actions: ActionsGroupWrapper) : BaseWebNodeWrapper<WebBorderContainer>(), EventsSubscriber , ListWrapper<BaseIdentityJS>{

    private lateinit var grid :WebDataGrid<BaseIdentityJS>
    private val objectTypes = arrayListOf<String>()

    init {
        DomainMetaRegistryJS.get().indexes[we.listId+"JS"]?.let { objectTypes.add(it.document) }
        DomainMetaRegistryJS.get().assets[we.listId+"JS"]?.let { objectTypes.add(it.id) }
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val searchBox = WebUiLibraryAdapter.get().createSearchBox {
            width ="200px"
        }
        val filterPanel = FilterPanel(we) {
            grid.reload()
        }
        grid = createGrid(we, searchBox, filterPanel)
        _node.setEastRegion{
            width = DefaultUIParameters.controlWidth + 10
            showSplitLine = true
            collapsible = true
            collapsed = true
            title = WebMessages.Filters
            content = filterPanel
        }
        _node.setCenterRegion{
            content = grid
        }
        val container = WebGridLayoutWidget{
            width = "100%"
        }.also {
            val widths = actions.actions.map { "auto" }.toMutableList().also{ lst ->
                lst.add("100%")
                lst.add("auto")
            }
            it.setColumnsWidths(widths)
            val cells = actions.actions.map {action ->
                val button = WebUiLibraryAdapter.get().createLinkButton{
                    title = action.displayName
                }
                if(action is ActionWrapper){
                    button.setHandler {
                        action.getActionHandler<ListLinkButtonHandler<BaseIdentityJS>>().invoke(grid.getSelected())
                    }
                }
                button
            }.toMutableList<WebNode?>().also {lst ->
                lst.add(null)
                lst.add(searchBox)
            }
            it.addRow(cells)
        }

        _node.setNorthRegion {
            content = container
        }
        searchBox.setSearcher {
            grid.reload()
        }
    }

    private fun createGrid(we: ListWorkspaceItemDTJS,  searchBox: WebSearchBox, filterPanel: FilterPanel): WebDataGrid<BaseIdentityJS> {
        val listId = "${we.listId}JS"
        val domainDescr: BaseIndexDescriptionJS =
                DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
                ?: throw IllegalArgumentException("no description found for $listId")


        val dataGrid = WebUiLibraryAdapter.get().createDataGrid<BaseIdentityJS> {
            fit = true
            showPagination = true
            we.columns.forEach { col ->
                val propertyDescr = domainDescr.properties[col]
                val collectionDescr = domainDescr.collections[col]
                if (propertyDescr == null && collectionDescr == null) {
                    throw IllegalArgumentException("no field description found for id $col")
                }
                val type = propertyDescr?.type
                val number = type == DatabasePropertyTypeJS.BIG_DECIMAL || type == DatabasePropertyTypeJS.LONG || type == DatabasePropertyTypeJS.INT

                column {
                    fieldId = col
                    title = propertyDescr?.displayName ?: collectionDescr!!.displayName!!
                    sortable = propertyDescr != null
                    horizontalAlignment = if (number) WebDataHorizontalAlignment.RIGHT else WebDataHorizontalAlignment.LEFT
                    resizable = true
                    formatter = MiscUtilsJS.createListFormatter(type)
                }
            }
        }
        dataGrid.setLoader { request ->
            val req = GetListRequestJS()
            req.columns.addAll(we.columns)
            req.criterions.addAll(we.criterions)
            req.desc = request.desc
            req.listId = we.listId!!
            req.page = request.page
            req.rows = request.rows
            req.sortColumn = request.sortColumn
            req.freeText = searchBox.getValue()
            req.filters.addAll(filterPanel.getFiltersValues())
            StandardRestClient.standard_standard_getList(req).let {
                WebDataGridResponse(it.totalCount!!, it.items)
            }
        }
        dataGrid.setRowDblClickListener {
            if(it is BaseIndexJS){
                MainFrame.get().openTab(OpenObjectData(it.document!!.type, it.document!!.uid, it.uid))
            }
        }
        return dataGrid
    }

    override fun receiveEvent(event: Any) {
            if(event is ObjectDeleteEvent){
                if(objectTypes.contains(event.objectType)){
                    grid.reload()
                }
            }
            if(event is ObjectModificationEvent){
                if(objectTypes.contains(event.objectType)){
                    grid.reload()
                }
            }
    }

    override fun getSelectedItems(): List<BaseIdentityJS> {
        return getSelectedItems()
    }
}

internal class FilterPanel(private val listItem:ListWorkspaceItemDTJS, private val applyCallback: suspend ()->Unit):BaseWebNodeWrapper<WebBorderContainer>(){

    private val filters = arrayListOf<FilterData>()

    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setSouthRegion {
            content = createButtons()
        }
        _node.setCenterRegion {
            content = createFilters()
        }
    }

    private fun createFilters(): WebGridLayoutWidget {
        return WebGridLayoutWidget{
            width = "100%"
        }.also { widget ->
            widget.setColumnsWidths("100%")
            val listId = "${listItem.listId}JS"
            val domainDescr: BaseIndexDescriptionJS =
                DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
                ?: throw IllegalArgumentException("no description found for $listId")
            listItem.filters.forEach {
                val label = WebLabelWidget(domainDescr.properties[it]?.displayName ?: domainDescr.collections[it]!!.displayName)
                widget.addRow(label)

                val handler:ListFilterHandler<*,*>? = when(domainDescr.properties[it]?.type){
                    DatabasePropertyTypeJS.STRING, DatabasePropertyTypeJS.TEXT ->
                        StringFilterHandler()
                    DatabasePropertyTypeJS.BOOLEAN ->
                        BooleanFilterHandler()
                    DatabasePropertyTypeJS.LOCAL_DATE ->
                        DateFilterHandler()
                    DatabasePropertyTypeJS.LOCAL_DATE_TIME ->
                        DateTimeFilterHandler()
                    DatabasePropertyTypeJS.BIG_DECIMAL ->
                        FloatNumberFilterHandler()
                    DatabasePropertyTypeJS.ENUM ->{
                        EnumValueFilterHandler(domainDescr.properties[it]!!.className!!)
                    }
                    DatabasePropertyTypeJS.ENTITY_REFERENCE ->{
                        EntityValuesFilterHandler(domainDescr.properties[it]!!.className!!)
                    }
                    else -> null
                }
                if(handler != null){
                    handler as ListFilterHandler<BaseListFilterValueDTJS, WebNode>
                    val component = handler.createEditor()
                    widget.addRow(component)
                    filters.add(FilterData(it, component, handler))
                }
            }
        }

    }

    private fun createButtons(): WebGridLayoutWidget {
        return WebGridLayoutWidget{
            width = "100%"
        }.also {
            it.setColumnsWidths("50%","50%")
            val applyCell = WebUiLibraryAdapter.get().createLinkButton {
                width = "100%"
                title = WebMessages.apply
            }.apply {
                setHandler {
                    applyCallback.invoke()
                }
            }
            val resetCell = WebUiLibraryAdapter.get().createLinkButton {
                width = "100%"
                title =  WebMessages.reset
            }.apply {
                setHandler {
                    filters.forEach { fd ->
                        fd.handler.reset(fd.comp)
                    }
                }
            }
            it.addRow(WebGridLayoutWidgetCell(applyCell), WebGridLayoutWidgetCell(resetCell))
        }
    }

    fun getFiltersValues(): List<ListFilterDTJS> {
        return filters.filter { it.handler.isNotEmpty(it.comp) }.map {
            val res = ListFilterDTJS()
            res.fieldId = it.fieldId
            res.value = it.handler.getValue(it.comp)
            res
        }.toList()
    }

}

internal class FilterData(val fieldId: String, val comp: WebNode, val handler: ListFilterHandler<BaseListFilterValueDTJS, WebNode>)