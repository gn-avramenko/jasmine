/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.DatabasePropertyTypeDTJS
import com.gridnine.jasmine.server.standard.model.rest.GetListRequestJS
import com.gridnine.jasmine.server.standard.model.rest.ListFilterDTJS
import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.*
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiListButtonWidget
import kotlin.js.Date
import kotlin.js.Promise

@Suppress("UNUSED_VARIABLE", "UnsafeCastFromDynamic")
class EasyUiListTabHandler(private val element: ListWorkspaceItemDTJS) : EasyUiTabHandler<Unit> {

    private val filterHandlers = arrayListOf<EasyUiListFilter<*>>()

    private val listDescr = UiMetaRegistryJS.get().lists[element.listId]
            ?: throw IllegalArgumentException("unable to find description for ${element.listId}")
    private val listIdJs = "${listDescr.objectId}JS"

    override fun getId(): String {
        return "list-${element.displayName}"
    }

    override fun getData(uid: String): Promise<Unit> {
        return Promise { resolve, _ ->
            resolve(Unit)
        }
    }

    override fun getTitle(data: Unit): String {
        return element.displayName ?: "???"
    }

    override fun getContent(data: Unit, uid: String): String {
        val descr = UiMetaRegistryJS.get().lists[element.listId]
                ?: throw IllegalArgumentException("unable to find description for ${element.listId}")

        return HtmlUtilsJS.html {
            div(`class` = "easyui-layout", data_options = "fit:true") {
                div(region = "north", border = false, `class` = "group wrap header", style = "height:45px;font-size:100%;padding:5px") {
                    div(`class` = "content") {
                        div(id = "buttons${uid}", style = "float:left") {
                        }
                        div(style = "float:right") {
                            input(id = "search${uid}", `class` = "easyui-searchbox", style = "width:200px")
                        }
                    }
                }
                div(id = "panel${uid}", region = "center", border = false) {
                    table(id = "table${uid}", `class` = "easyui-datagrid", data_options = "border:false,singleSelect:true,pagination: true,fit:true") {}
                }
                if (element.filters.isNotEmpty()) {
                    div(id = "filters${uid}", region = "east", border = true, data_options = "title:'Фильтры',collapsed:true,hideCollapsedContent:false", style = "width:220px") {
                        val objectId = descr.objectId+"JS"
                        val domainDescr: BaseIndexDescriptionJS =
                                DomainMetaRegistryJS.get().indexes[objectId] ?: DomainMetaRegistryJS.get().assets[objectId]
                                ?: throw IllegalArgumentException("no description found for $objectId")
                        element.filters.forEach { filter ->
                            val propertyDescription = domainDescr.properties[filter]
                            val collectionDescription = domainDescr.collections[filter]
                            if (propertyDescription == null && collectionDescription == null) {
                                throw IllegalArgumentException("unable to find filter description $filter")
                            }
                            div(style="padding:5px") { (propertyDescription?.displayName ?: collectionDescription!!.displayName)() }
                            if (propertyDescription != null) {
                                when (propertyDescription.type) {
                                    DatabasePropertyTypeJS.LOCAL_DATE,
                                    DatabasePropertyTypeJS.LOCAL_DATE_TIME,
                                    DatabasePropertyTypeJS.BIG_DECIMAL,
                                    DatabasePropertyTypeJS.INT,
                                    DatabasePropertyTypeJS.LONG -> {
                                        div(style = "width:100%;padding:5px") { "<input id = \"filter${filter}from${uid}\" style=\"width:100%\">"() }
                                        div(style = "width:100%;padding:5px") { "<input id = \"filter${filter}to${uid}\" style=\"width:100%\">"() }
                                    }
                                    else -> div(style = "width:100%;padding:5px") { "<input id = \"filter${filter}${uid}\" style=\"width:100%\">"() }
                                }
                            } else {
                                when (collectionDescription!!.elementType) {
                                    DatabaseCollectionTypeJS.STRING -> TODO()
                                    DatabaseCollectionTypeJS.ENUM -> TODO()
                                    DatabaseCollectionTypeJS.ENTITY_REFERENCE -> TODO()
                                }
                            }
                        }
                        div(style = "width:100%;padding:5px") {
                            "<a style = \"float:left\"' id=\"resetFilters${uid}\" href=\"#\">Сбросить</a>"()
                            "<a style = \"float:right\"' id=\"applyFilters${uid}\" href=\"#\">Применить</a>"()
                        }
                    }
                }
            }
        }.toString()
    }

    override fun decorateData(data: Unit, uid: String, setTitle: (String) -> Unit, close: () -> Unit) {
        val datagridDiv = jQuery("#table${uid}")
        val searchboxdiv = jQuery("#search${uid}").searchbox(object {
            val prompt = "поиск"
            val searcher = { _: String, _: String ->
                datagridDiv.datagrid("load")
            }
        })
        val selectionChangeListeners = arrayListOf<(List<BaseEntityJS>)->Unit>()
        val list = object:EntityList<BaseEntityJS>{


            override fun getSelectedElements(): List<BaseEntityJS> {
                return getSelectedElements(datagridDiv)
            }

            override fun addSelectionChangeListener(listener: (List<BaseEntityJS>) -> Unit) {
                selectionChangeListeners.add(listener)
            }

            override fun getListId(): String {
                return listIdJs
            }

            override fun reload() {
                datagridDiv.datagrid("load")
            }

        }
        val descriptions = hashMapOf<BaseListToolButtonHandler<BaseEntityJS>, BaseToolButtonDescriptionJS>()
        val toolButtonHandlers = arrayListOf<BaseListToolButtonHandler<BaseEntityJS>>()
        UiMetaRegistryJS.get().sharedListToolButtons.forEach {
            val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as SharedListToolButtonHandler<BaseEntityJS>
            if(handler.isApplicableToList(listIdJs)){
                toolButtonHandlers.add(handler)
                descriptions[handler] =it
            }
        }
        listDescr.toolButtons.forEach {
            val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as BaseListToolButtonHandler<BaseEntityJS>
            toolButtonHandlers.add(handler)
            descriptions[handler] =it
        }
        toolButtonHandlers.sortBy { descriptions[it]!!.weight }
        val buttonsContent = HtmlUtilsJS.html {
            toolButtonHandlers.forEach {
                val descr = descriptions[it]!!
                a(href = "#", id = "${descr.id}${uid}") {
                    descr.displayName()
                }
            }
        }.toString()
        jQuery("#buttons${uid}").html(buttonsContent)
        val widgets = hashMapOf<BaseListToolButtonHandler<BaseEntityJS>, ToolButtonWidget>()
        toolButtonHandlers.forEach {
            val descr = descriptions[it]!!
            widgets[it] = EasyUiListButtonWidget("${descr.id}${uid}", it, list)
        }

        list.addSelectionChangeListener{
            updateVisibility(toolButtonHandlers,  widgets, list)
        }

        val domainDescr: BaseIndexDescriptionJS =
                DomainMetaRegistryJS.get().indexes[listIdJs] ?: DomainMetaRegistryJS.get().assets[listIdJs]
                ?: throw IllegalArgumentException("no description found for $listIdJs")

        val columns = arrayListOf<Any>()

        element.columns.forEach { columnId ->
            val propertyDescr = domainDescr.properties[columnId]
            val collectionDescr = domainDescr.collections[columnId]
            if (propertyDescr == null && collectionDescr == null) {
                throw IllegalArgumentException("no field description found for id $columnId")
            }
            val type = propertyDescr?.type ?: collectionDescr!!.elementType
            val number = type == DatabasePropertyTypeDTJS.BIG_DECIMAL || type == DatabasePropertyTypeDTJS.LONG || type == DatabasePropertyTypeDTJS.INT
            columns.add(object {
                val field = columnId
                val title = propertyDescr?.displayName ?: collectionDescr!!.displayName
                val sortable = propertyDescr != null
                val align = if (number) "right" else "left"
                val resizable = true
                val width = 150
                val formatter = createListColumnFormatter(type)
            }
            )
        }


        datagridDiv.datagrid(object {
            private var ignoreSelect = false
            val fitColumns = true
            val fit = true
            val columns = /*js("[[{field:'login'," +
                    "title:'Login', width:150}]]")*/arrayOf(columns.toTypedArray())
            val onDblClickRow = { _: Int, row: dynamic ->
                val doc = row["document"]
                if(doc is EntityReferenceJS){
                    MainFrame.get().openTab(doc.type, doc.uid)
                }
            }
            private val onSelectionChanged = onSelectionChanged@ {
                val selected = getSelectedElements(datagridDiv)
                selectionChangeListeners.forEach { it(selected) }
            }
            val onSelect = onSelect@ {
                if(ignoreSelect){
                    return@onSelect
                }
                ignoreSelect = true
                onSelectionChanged()
                ignoreSelect = false

            }
            val onUnSelect = onSelect@ {
                if(ignoreSelect){
                    return@onSelect
                }
                ignoreSelect = true
                onSelectionChanged()
                ignoreSelect = false
            }
            val onLoadSuccess ={
                updateVisibility(toolButtonHandlers, widgets, list)
            }
            val loader = { params: dynamic, success: dynamic, _: dynamic ->

                val request = GetListRequestJS()
                request.listId = listDescr.objectId
                request.columns.addAll(element.columns)
                request.sortColumn = params.sort
                request.desc = "desc" == params.order
                request.criterions.addAll(element.criterions)
                request.freeText = searchboxdiv.searchbox("getValue") as String?
                request.rows = params.rows
                request.page = params.page
                filterHandlers.forEach { handler ->
                    handler.getFilterValue()?.let {
                        val filter = ListFilterDTJS()
                        filter.fieldId = handler.getFieldId()
                        filter.value = it
                        request.filters.add(filter)
                    }
                }
                StandardRestClient.standard_standard_list(request).then { response ->
                    val rows = arrayOfNulls<Any>(response.items.size)
                    response.items.withIndex().forEach { (index, idx) ->
                        val item = js("{}")
                        element.columns.forEach {
                            item[it] = idx.getValue(it)
                        }
                        if (idx is BaseIndexJS) {
                            item["document"] = idx.document
                        } else {
                            item["uid"] = idx.uid
                        }
                        rows[index] = item
                    }
                    success(object {
                        val total = response.totalCount
                        val rows = rows
                    })
                }
            }
        })



        updateVisibility(toolButtonHandlers,  widgets, list)

        jQuery("#resetFilters${uid}").linkbutton(object{
            val onClick = {
                filterHandlers.forEach { it.resetFilter() }
            }
        })
        jQuery("#applyFilters${uid}").linkbutton(object {
            val onClick = {
                jQuery("#table${uid}").datagrid("load")
            }
        })
        element.filters.forEach { filter ->
            val propertyDescription = domainDescr.properties[filter]
            val collectionDescription = domainDescr.collections[filter]
            if (propertyDescription == null && collectionDescription == null) {
                throw IllegalArgumentException("unable to find filter description $filter")
            }
            if(propertyDescription != null){
                filterHandlers.add(
                        when(propertyDescription.type){
                            DatabasePropertyTypeJS.STRING,
                            DatabasePropertyTypeJS.TEXT -> EasyUiListStringFilter(filter, "filter${filter}${uid}")

                            DatabasePropertyTypeJS.LOCAL_DATE -> EasyUiListLocalDateFilter(filter, "filter${filter}from${uid}", "filter${filter}to${uid}")
                            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> EasyUiListLocalDateTimeFilter(filter, "filter${filter}from${uid}", "filter${filter}to${uid}")
                            DatabasePropertyTypeJS.ENUM -> EasyUiListEnumFilter(filter, propertyDescription.className!!, "filter${filter}${uid}")
                            DatabasePropertyTypeJS.BOOLEAN -> EasyUiListBooleanFilter(filter, "filter${filter}${uid}")
                            DatabasePropertyTypeJS.ENTITY_REFERENCE -> EasyUiListEntityFilter(filter, propertyDescription.className!!, "filter${filter}${uid}")
                            DatabasePropertyTypeJS.LONG, DatabasePropertyTypeJS.INT -> EasyUiListIntFilter(filter, "filter${filter}from${uid}", "filter${filter}to${uid}")
                            DatabasePropertyTypeJS.BIG_DECIMAL -> EasyUiListFloatFilter(filter, "filter${filter}from${uid}", "filter${filter}to${uid}")
                        }
                )
            } else {
                when (collectionDescription!!.elementType) {
                    DatabaseCollectionTypeJS.STRING -> TODO()
                    DatabaseCollectionTypeJS.ENUM -> TODO()
                    DatabaseCollectionTypeJS.ENTITY_REFERENCE -> TODO()
                }
            }
        }
    }

    private fun updateVisibility(toolButtonHandlers: ArrayList<BaseListToolButtonHandler<BaseEntityJS>>,  widgets: HashMap<BaseListToolButtonHandler<BaseEntityJS>, ToolButtonWidget>, list: EntityList<BaseEntityJS>) {
        toolButtonHandlers.forEach { handler ->
            widgets[handler]!!.setVisible(handler.isVisible(list))
            widgets[handler]!!.setEnabled(handler.isEnabled(list))
        }
    }

    private fun getSelectedElements(datagridDiv: JQuery): List<BaseEntityJS> {
        val selectedRows = datagridDiv.datagrid("getSelections") as Array<dynamic>
        if(selectedRows.isEmpty()){
            return emptyList()
        }
        val selections = arrayListOf<BaseEntityJS>()
        selectedRows.forEach{
            val row = ReflectionFactoryJS.get().getFactory(listIdJs)() as BaseEntityJS
            if(row is BaseIndexJS){
                row.setValue(BaseIndexJS.document, it[BaseIndexJS.document])
            } else {
                row.setValue(BaseEntityJS.uid, it[BaseEntityJS.uid])
            }
            element.columns.forEach { columnId ->
                row.setValue(columnId, it[columnId])
            }
            selections.add(row)
        }
        return selections

    }

    private fun createListColumnFormatter(type: Any, maxLength: Int = 50): (value: dynamic, row: dynamic, index: Int) -> String {
        return { value: dynamic, _: dynamic, _: Int ->
            lateinit var displayName: String
            displayName = if (value is Enum<*>) {
                val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(value::class)
                val enumDescr = DomainMetaRegistryJS.get().enums[qualifiedName]
                if (enumDescr != null) {
                    enumDescr.items[value.name]!!.displayName
                } else {
                    value.name
                }

            } else if (value?.caption != null) {
                value.caption as String
            } else if (type == DatabasePropertyTypeJS.LOCAL_DATE) {
                dateFormatter(value as Date?) ?: "???"
            } else if (type == DatabasePropertyTypeJS.LOCAL_DATE_TIME) {
                dateTimeFormatter(value as Date?) ?: "???"
            } else {
                value?.toString() ?: ""
            }
            if (displayName.length > maxLength) {
                displayName = "<span title=\"$displayName\" class=\"easyui-tooltip\">${displayName.substring(0, 50)} ...</span>"
            }
            displayName
        }
    }

    private val dateFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth() + 1)}-${TextUtilsJS.fillWithZeros(it.getDate())}" }
    }

    private val dateTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth() + 1)}-${TextUtilsJS.fillWithZeros(it.getDate())} ${TextUtilsJS.fillWithZeros(it.getHours())}:${TextUtilsJS.fillWithZeros(it.getMinutes())}" }
    }



}