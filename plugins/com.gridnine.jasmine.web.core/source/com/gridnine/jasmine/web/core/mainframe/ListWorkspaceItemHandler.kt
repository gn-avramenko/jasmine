/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.server.core.model.domain.BaseIndexDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.BaseIndexJS
import com.gridnine.jasmine.server.core.model.domain.DatabasePropertyTypeJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS
import com.gridnine.jasmine.server.standard.model.rest.GetListRequestJS
import com.gridnine.jasmine.server.standard.model.rest.ListFilterDTJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.ui.widgets.SearchBoxWidget
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Promise

class ListWorkspaceItemHandler : MainFrameTabHandler<ListWorkspaceItemJS, Unit> {


    override fun loadData(obj: ListWorkspaceItemJS): Promise<Unit> {
        return Promise { resolve, reject ->
            resolve.invoke(Unit)
        }
    }

    class ListPanel:WebComponent, EventsSubscriber, WebPopupContainer{
        private val par: WebComponent
        private val borderLayout:WebBorderContainer
        private lateinit var grid:WebDataGrid<*>
        private val objectTypes = arrayListOf<String>()
        private val objectlist :ObjectsList<BaseIntrospectableObjectJS>
        private val listButtonsMap= hashMapOf<WebLinkButton, ListButtonHandler<BaseIntrospectableObjectJS>>()
        constructor(we: ListWorkspaceItemJS,parent: WebComponent, callback: MainFrameTabCallback){
            DomainMetaRegistryJS.get().indexes[we.listId+"JS"]?.let { objectTypes.add(it.document) }
            DomainMetaRegistryJS.get().assets[we.listId+"JS"]?.let { objectTypes.add(it.id) }
            par = parent
            borderLayout = UiLibraryAdapter.get().createBorderLayout(parent) {
                fit = true
            }

            val filterPanel = FilterPanel(we, borderLayout, {
                grid.reload()
            })
            borderLayout.setEastRegion(WebBorderContainer.region {
                width = DefaultUIParameters.controlWidth + 10
                showSplitLine = true
                collapsible = true
                collapsed = true
                title = CoreWebMessagesJS.filters
                content = filterPanel
            })
            val searchBox = SearchBoxWidget(parent) {
                width = "${DefaultUIParameters.controlWidth}px"
            }
            grid = createGrid(we, this, searchBox, filterPanel)
            borderLayout.setCenterRegion(WebBorderContainer.region {
                content = grid
            })
            objectlist = object : ObjectsList<BaseIntrospectableObjectJS> {
                override fun getDataGrid(): WebDataGrid<BaseIntrospectableObjectJS> {
                    return grid as WebDataGrid<BaseIntrospectableObjectJS>
                }
            }
            val container = UiLibraryAdapter.get().createGridLayoutContainer(borderLayout) {
                width = "100%"
            }
            val listHandlers = ListButtonHandlersCache.get().getListButtonHandlers(we.listId!!)
            listHandlers.forEach { lh ->
                container.defineColumn("auto")
            }
            container.defineColumn("100%")
            container.defineColumn("auto")
            container.addRow()
            listHandlers.forEach {lH ->
                val button = UiLibraryAdapter.get().createLinkButton(container){
                    title = lH.getDisplayName()
                }
                button.setHandler {
                    lH.onClick(objectlist)
                }
                button.setEnabled(lH.isEnabled(objectlist))
                listButtonsMap[button] = lH
                container.addCell(WebGridLayoutCell(button, 1))
            }
            container.addCell(WebGridLayoutCell(null, 1))
            grid.setSelectionChangeListener {
                updateButtonsVisibility()
            }

            container.addCell(WebGridLayoutCell(searchBox, 1))
            borderLayout.setNorthRegion(WebBorderContainer.region {
                content = container
            })
            searchBox.setSearcher {
                grid.reload()
            }
        }

        private fun updateButtonsVisibility() {
            listButtonsMap.entries.forEach {
                it.key.setEnabled(it.value.isEnabled(objectlist))
            }
        }

        private fun createGrid(we: ListWorkspaceItemJS, parent: WebComponent, searchBox: SearchBoxWidget, filterPanel: FilterPanel): WebDataGrid<*> {
            val listId = "${we.listId}JS"
            val domainDescr: BaseIndexDescriptionJS =
                    DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
                    ?: throw IllegalArgumentException("no description found for $listId")


            val dataGrid = UiLibraryAdapter.get().createDataGrid<BaseIntrospectableObjectJS>(parent) {
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
                        title = propertyDescr?.displayName ?: collectionDescr!!.displayName
                        sortable = propertyDescr != null
                        horizontalAlignment = if (number) WebDataHorizontalAlignment.RIGHT else WebDataHorizontalAlignment.LEFT
                        resizable = true
                        formatter = ListWorkspaceItemHandler.createFormatter(type)
                    }
                }
            }
            dataGrid.setLoader { request ->
                Promise { resolve, _ ->
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
                    StandardRestClient.standard_standard_getList(req).then {
                        val res = WebDataGridResponse(it.totalCount!!, it.items)
                        resolve.invoke(res as WebDataGridResponse<BaseIntrospectableObjectJS>)
                        window.setTimeout(ListWorkspaceItemHandler@this::updateButtonsVisibility, 100)
                    }
                }
            }
            dataGrid.setRowDblClickListener {
                if(it is BaseIndexJS){
                    MainFrame.get().openTab(it.document, navigationKey = it.uid)
                }
            }
            return dataGrid
        }

        override fun getId(): String {
            return borderLayout.getId()
        }

        override fun getParent(): WebComponent? {
            return par
        }

        override fun getChildren(): List<WebComponent> {
            return borderLayout.getChildren()
        }

        override fun getHtml(): String {
            return borderLayout.getHtml()
        }

        override fun decorate() {
            borderLayout.decorate()
        }

        override fun destroy() {
            borderLayout.destroy()
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

        fun simulateButtonClick(buttonId:String):Promise<Any>{
            val handler = listButtonsMap.values.find { it.getId() == buttonId }
            handler as TestableListButtonHandler<BaseIntrospectableObjectJS, Any>
            return handler.onTestClick(objectlist)
        }


    }
    override fun createTabData(we: ListWorkspaceItemJS, data: Unit, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(we.displayName ?: "???", ListPanel(we, parent, callback))
    }




    override fun getTabId(obj: ListWorkspaceItemJS): String {
        return "list${obj.uid}"
    }

    companion object {
        private val dateFormatter = { date: Date? ->
            date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth() + 1)}-${MiscUtilsJS.fillWithZeros(it.getDate())}" }
        }

        private val dateTimeFormatter = { date: Date? ->
            date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth() + 1)}-${MiscUtilsJS.fillWithZeros(it.getDate())} ${MiscUtilsJS.fillWithZeros(it.getHours())}:${MiscUtilsJS.fillWithZeros(it.getMinutes())}" }
        }

        fun createFormatter(type: DatabasePropertyTypeJS?) =
                { value: Any?, row: BaseIntrospectableObjectJS, index: Int ->
                    lateinit var displayName: String
                    displayName = if (value is Enum<*>) {
                        val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(value::class)
                        val enumDescr = DomainMetaRegistryJS.get().enums[qualifiedName]
                        if (enumDescr != null) {
                            enumDescr.items[value.name]!!.displayName
                        } else {
                            value.name
                        }

                    } else if (value is Boolean) {
                        if (value) CoreWebMessagesJS.YES else CoreWebMessagesJS.NO
                    } else if (value?.asDynamic()?.caption != null) {
                        value?.asDynamic()?.caption as String
                    } else if (type == DatabasePropertyTypeJS.LOCAL_DATE) {
                        dateFormatter(value as Date?) ?: "???"
                    } else if (type == DatabasePropertyTypeJS.LOCAL_DATE_TIME) {
                        dateTimeFormatter(value as Date?) ?: "???"
                    } else {
                        value?.toString() ?: ""
                    }
                    if (displayName.length > 100) {
                        displayName = "<span title=\"$displayName\" class=\"easyui-tooltip\">${displayName.substring(0, 50)} ...</span>"
                    }
                    displayName
                }
    }

    class FilterPanel(private val we: ListWorkspaceItemJS, private val parent: WebComponent, private val applyCallback:()->Unit, private val delegate: WebBorderContainer = UiLibraryAdapter.get().createBorderLayout(parent) { fit = true }) : WebBorderContainer by delegate {

        private var initialized2 = false

        private val filters = arrayListOf<FilterData>()

        val listId = "${we.listId}JS"
        val domainDescr: BaseIndexDescriptionJS =
                DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
                ?: throw IllegalArgumentException("no description found for $listId")

        init {
            setCenterRegion(WebBorderContainer.region {
                showBorder = false
                showSplitLine = false
                content = createFilters()
            })
            setSouthRegion(WebBorderContainer.region {
                showBorder = false
                showSplitLine = false
                content = createButtons()
            })
        }

        private fun createFilters(): WebComponent {
            val container = UiLibraryAdapter.get().createGridLayoutContainer(this) {
                width = "100%"
            }
            container.defineColumn("100%")
            we.filters.forEach {
                container.addRow()
                val label = UiLibraryAdapter.get().createLabel(container)
                label.setText(domainDescr.properties[it]?.displayName ?: domainDescr.collections[it]!!.displayName)
                container.addCell(WebGridLayoutCell(label))
                val handler = when (domainDescr.properties[it]?.type) {
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
                if (handler != null) {
                    val component = handler.createEditor(container)
                    container.addRow()
                    container.addCell(WebGridLayoutCell(component))
                    filters.add(FilterData(it, component, handler as ListFilterHandler<BaseListFilterValueDTJS, WebComponent>))
                }
            }
            return container
        }

        private fun createButtons(): WebComponent {
            val container = UiLibraryAdapter.get().createGridLayoutContainer(this) {
                width = "100%"
            }
            container.defineColumn("50%")
            container.defineColumn("50%")
            container.addRow()
            val applyButton = UiLibraryAdapter.get().createLinkButton(this) {
                width = "100%"
                title = CoreWebMessagesJS.apply
            }
            applyButton.setHandler {
                applyCallback.invoke()
            }
            container.addCell(WebGridLayoutCell(applyButton))
            val resetButton = UiLibraryAdapter.get().createLinkButton(this) {
                width = "100%"
                title = CoreWebMessagesJS.reset
            }
            resetButton.setHandler {
                filters.forEach {
                    it.handler.reset(it.comp)
                }
            }
            container.addCell(WebGridLayoutCell(resetButton))
            return container
        }

        fun getFiltersValues(): List<ListFilterDTJS> {
            if(!initialized2){
                return emptyList()
            }
            return filters.filter { it.handler.isNotEmpty(it.comp) }.map {
                val res = ListFilterDTJS()
                res.fieldId = it.fieldId
                res.value = it.handler.getValue(it.comp)
                res
            }.toList()
        }

        override fun decorate() {
            delegate.decorate()
            initialized2 = true
        }
    }


    internal class FilterData(val fieldId: String, val comp: WebComponent, val handler: ListFilterHandler<BaseListFilterValueDTJS, WebComponent>)
}

class ListButtonHandlersCache{

    fun getListButtonHandlers(objectId:String):List<ListButtonHandler<BaseIntrospectableObjectJS>>{
        if(!listButtonHandlersCache.containsKey(objectId)){
            updateObjectsButtonsCaches(objectId)
        }
        return listButtonHandlersCache[objectId]!!
    }

    private fun updateObjectsButtonsCaches(objectId: String) {
        val list = ClientRegistry.get().allOf(ListButtonHandler.TYPE).filter { it.isApplicable(objectId) }.sortedBy { it.getWeight() }
        listButtonHandlersCache[objectId] = list as List<ListButtonHandler<BaseIntrospectableObjectJS>>
    }


    companion object{
        private val listButtonHandlersCache = hashMapOf<String, List<ListButtonHandler<BaseIntrospectableObjectJS>>>()
        fun get() = EnvironmentJS.getPublished(ListButtonHandlersCache::class)
    }
}