/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

@Suppress("UnsafeCastFromDynamic")
class EasyUiEntityMultiSelectWidget(uid:String, description:EntitySelectDescriptionJS):EntityMultiSelectWidget(){
    private val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized  = false
    private val selectedValues = hashSetOf<SelectItemJS>()
    var spanElm:dynamic = null
    private var ignoreSearchRequest = false
    init {

        configure = {settings:EntitySelectConfigurationJS ->
            if (!initialized) {
                ignoreSearchRequest = true
                val options = object {
                    val valueField = "id"
                    val textField = "caption"
                    val editable = true
                    val limitToList = true
                    val hasDownArrow =  true
                    val mode = "remote"
                    val onChange = { newValue: Array<String>, _: Array<String>? ->
                        selectedValues.clear()
                        newValue.forEach{selectedValues.add(toSelectItem(toReference(it)))}
                        div.tagbox("getIcon",0).asDynamic().css("visibility",if(selectedValues.isEmpty()) "hidden" else "visible")
                        if (spanElm != null) {
                            spanElm.css("border-color", "")
                            spanElm.removeAttr("title")
                        }
                    }
                    val loader = loader@ {	param:dynamic,success:dynamic,_:dynamic ->
                        if(ignoreSearchRequest){
                            success(selectedValues.toMutableList().sortedBy { it.caption }.toTypedArray())
                            return@loader true
                        }

                        val request = EntityAutocompleteRequestJS()
                        request.limit =settings.limit
                        request.searchText = param.q
                        settings.dataSources.forEach {
                            val autocompleteDescription = UiMetaRegistryJS.get().autocompletes[it]?:throw IllegalArgumentException("unable to find autocomplete for dataSource $it")
                            request.entitiesIds.add(autocompleteDescription.entity)

                        }
                        StandardRestClient.standard_standard_defaultAutocomplete(request).then { response ->
                            val set = response.items.map { toSelectItem(it) }.toMutableSet()
                            set.addAll(selectedValues)
                            val result = set.toMutableList().sortedBy { it.caption }
                            success(result.toTypedArray())
                        }
                        return@loader true
                    }
                    val icons = arrayOf(object{
                        val iconCls = "icon-clear"
                        val handler = {_:dynamic ->
                            ignoreSearchRequest = true
                            selectedValues.clear()
                            div.tagbox("setValues", arrayOfNulls<String>(0))
                            ignoreSearchRequest = false
                        }
                    })
                }
                div.tagbox(options)
                spanElm = div.tagbox("textbox").asDynamic().parent()
                div.tagbox("getIcon",0).asDynamic().css("visibility", "hidden")
                ignoreSearchRequest = false
            }
        }
        readData = { values ->
            ignoreSearchRequest = true
            selectedValues.clear()
            values.forEach { selectedValues.add(toSelectItem(it)) }
            if(values.isEmpty()){
                div.tagbox("setValue", null)
                div.tagbox("setText", null)
            } else{
                div.tagbox("loadData", selectedValues.toTypedArray())
                div.tagbox("setValues", values.map { toSelectItem(it).id}.toTypedArray())
            }
            ignoreSearchRequest = false
        }
        showValidation = {
            val spanElm = div.tagbox("textbox").asDynamic().parent()
            if (it == null) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            } else if (!spanElm.hasClass("text-field-error")) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            }
        }
        writeData ={data ->
            val values = div.tagbox("getValues") as Array<String>
            data.clear()
            values.forEach{data.add(toReference(it))}
        }
    }



    private fun toSelectItem(ref:EntityReferenceJS):SelectItemJS{
        return SelectItemJS("${ref.type}||${ref.uid}||${ref.caption}", ref.caption)
    }

    private fun toReference(item:String):EntityReferenceJS{
        val comps = item.split("||")
        return EntityReferenceJS(comps[0],comps[1],comps[2])
    }
}