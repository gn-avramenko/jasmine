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
class EasyUiEntitySelectWidget(uid:String, description:EntitySelectDescriptionJS):EntitySelectWidget(){
    private val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized  = false
    private var selectedValue:SelectItemJS? = null
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
                    val multiple = false
                    val mode = "remote"
                    val onChange = { newValue: String, _: String? ->
                        selectedValue = if(newValue.isNotBlank()) toSelectItem(toReference(newValue)) else null

                        div.combobox("getIcon",0).asDynamic().css("visibility",if(selectedValue == null) "hidden" else "visible")
                        if (spanElm != null) {
                            spanElm.css("border-color", "")
                            spanElm.removeAttr("title")
                        }
                    }
                    val loader = loader@ {	param:dynamic,success:dynamic,_:dynamic ->
                        if(ignoreSearchRequest){
                            selectedValue?.let { success(arrayOf(it))}?:success(emptyArray<SelectItemJS>())
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
                            selectedValue?.let { set.add(it) }
                            val result = set.toMutableList().sortedBy { it.caption }
                            success(result.toTypedArray())
                        }
                        return@loader true
                    }
                    val icons = arrayOf(object{
                        val iconCls = "icon-clear"
                        val handler = {_:dynamic ->
                            ignoreSearchRequest = true
                            selectedValue = null
                            div.combobox("setValues", arrayOfNulls<String>(0))
                            ignoreSearchRequest = false
                        }
                    })
                }
                div.combobox(options)
                spanElm = div.combobox("textbox").asDynamic().parent()
                div.combobox("getIcon",0).asDynamic().css("visibility", "hidden")
                ignoreSearchRequest = false
            }
        }

        setData = { value ->
            ignoreSearchRequest = true
            selectedValue = value?.let{toSelectItem(it)}
            if(selectedValue == null){
                div.combobox("setValue", null)
                div.combobox("setText", null)
            } else{
                div.combobox("loadData", arrayOf(selectedValue))
                div.combobox("setValues", arrayOf(selectedValue))
            }
            ignoreSearchRequest = false
        }
        showValidation = {
            val spanElm = div.combobox("textbox").asDynamic().parent()
            if (it == null) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            } else if (!spanElm.hasClass("text-field-error")) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            }
        }
        getData = {
            val values = div.combobox("getValues") as Array<String>
            if (values.isEmpty()) null else toReference(values[0])
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