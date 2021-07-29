/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.AutocompleteRequestJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.standard.StandardRestClient

class EntityMultiValuesWidget(configure:EntityMultiValuesWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebSelect>() {
    private val config = EntityMultiValuesWidgetConfiguration()

    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.REMOTE
            showClearIcon = config.showClearIcon
            editable = true
            multiple = true
            hasDownArrow = false
        }
        _node.setLoader { value ->
            val request = AutocompleteRequestJS()
            request.autocompleteFieldName = config.handler.getAutocompleteFieldName()
            request.listId = config.handler.getIndexClassName()
            request.limit = 10
            request.pattern = value
            StandardRestClient.standard_standard_autocomplete(request).items.map {
                toSelectItem(it.document)
            }
        }
    }

    fun getValues():List<ObjectReferenceJS>{
        return _node.getValues().map { toObjectReference(it) }
    }

    fun setValues(list: List<ObjectReferenceJS>) {
        _node.setValues(list.map { toSelectItem(it) })
    }

    fun showValidation(message:String?){
        _node.showValidation(message)
    }

    private fun toSelectItem(ref:ObjectReferenceJS): SelectItemJS {
        return SelectItemJS("${ref.type}||${ref.uid}", ref.caption?:"???")
    }
    private fun toObjectReference(item: SelectItemJS):ObjectReferenceJS{
        return ObjectReferenceJS(item.id.substringBefore("||"), item.id.substringAfter("||"), item.text)
    }
}

class EntityMultiValuesWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = false
    lateinit var handler: AutocompleteHandler
}