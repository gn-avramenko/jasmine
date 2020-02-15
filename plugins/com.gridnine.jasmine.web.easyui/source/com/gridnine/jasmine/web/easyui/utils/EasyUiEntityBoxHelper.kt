/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UnsafeCastFromDynamic")

package com.gridnine.jasmine.web.easyui.utils

import com.gridnine.jasmine.server.standard.model.rest.DatabasePropertyTypeDTJS
import com.gridnine.jasmine.server.standard.model.rest.GetListRequestJS
import com.gridnine.jasmine.server.standard.model.rest.ListFilterDTJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.domain.*
import com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.UiMetaRegistryJS
import com.gridnine.jasmine.web.core.ui.DialogButtonHandler
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import kotlin.browser.window
import kotlin.js.Date

object EasyUiEntityBoxHelper {
    fun selectEntity(settings: EntitySelectConfigurationJS, callback: (EntityReferenceJS) -> Unit) {
        var div = jQuery("#entity-select-dialog")
        if (div.length > 0) {
            div.remove()
        }
        val content = HtmlUtilsJS.html {
            div(id = "entity-select-dialog",  style = "display:none") {
                div(id = "entity-select-dialog-panel", `class` = "easyui-layout") {
                    div(region = "north", border = false, `class` = "group wrap header", style = "height:45px;font-size:100%;padding:5px") {
                        div(style = "float:right") {
                            input(id = "search-entity-select-dialog", `class` = "easyui-searchbox", style = "width:200px")
                        }
                    }
                    div(region = "center", border = false) {
                        table(id = "table-entity-select-dialog", `class` = "easyui-datagrid", data_options = "border:false,singleSelect:true,pagination: true,fit:true") {}
                    }
                }
            }
        }.toString()
        jQuery("body").append(content)
        div = jQuery("#entity-select-dialog")


        val datagridDiv = jQuery("#table-entity-select-dialog")
        div.dialog(object {
            val title = "Выбор объекта"
            val closed = false
            val cache = false
            val modal = true
            val closable = true
            val width = (window.innerWidth / 2).toInt()
            val height = (window.innerHeight / 2).toInt()
            val buttons = arrayOf(object {
                val text = "Выбрать"
                val handler = {
                    val selected = datagridDiv.datagrid("getSelected").asDynamic()
                    if(selected != null){
                        val doc = selected["document"]
                        if (doc is EntityReferenceJS) {
                            callback.invoke(doc)
                            div.dialog("close")
                        }
                    }
                }
            }, object {
                val text = "Закрыть"
                val handler = {
                    div.dialog("close")
                }
            })
        })
        jQuery("#entity-select-dialog-panel").layout(object {
            val fit = "true"
        })
        val searchboxdiv = jQuery("#search-entity-select-dialog").searchbox(object {
            val prompt = "поиск"
            val searcher = { _: String, _: String ->
                datagridDiv.datagrid("load")
            }
        })

        val autocompleteDecr = UiMetaRegistryJS.get().autocompletes[settings.dataSources[0]]
                ?: throw IllegalArgumentException("no autocomplete description found for ${settings.dataSources[0]}")
        val entityId = autocompleteDecr.entity + "JS"
        val domainDescr: BaseIndexDescriptionJS =
                DomainMetaRegistryJS.get().indexes[entityId] ?: DomainMetaRegistryJS.get().assets[entityId]
                ?: throw IllegalArgumentException("no description found for $entityId")

        val columns = arrayListOf<Any>()

        autocompleteDecr.columns.forEach { columnId ->
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
            val columns = arrayOf(columns.toTypedArray())
            val onDblClickRow = { _: Int, row: dynamic ->
                val doc = row["document"]
                if (doc is EntityReferenceJS) {
                    callback.invoke(doc)
                    div.dialog("close")
                }
            }
            val loader = { params: dynamic, success: dynamic, _: dynamic ->

                val request = GetListRequestJS()
                request.listId = domainDescr.id.substringBeforeLast("JS")
                request.columns.addAll(autocompleteDecr.columns)
                request.sortColumn = params.sort
                request.desc = "desc" == params.order
                request.freeText = searchboxdiv.searchbox("getValue") as String?
                request.rows = params.rows
                request.page = params.page
                StandardRestClient.standard_standard_list(request).then { response ->
                    val rows = arrayOfNulls<Any>(response.items.size)
                    response.items.withIndex().forEach { (index, idx) ->
                        val item = js("{}")
                        autocompleteDecr.columns.forEach {
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
