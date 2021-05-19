/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.EntitySelectBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.AutocompleteRequestJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.SelectDataType
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.MainFrame

class EntitySelectWidget(configure:EntitySelectWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebGridLayoutContainer>() {
    private val webSelect:WebSelect

    private val conf = EntitySelectWidgetConfiguration()
    init {
        conf.configure()
        webSelect =WebUiLibraryAdapter.get().createSelect{
            width = "100%"
            height = "100%"
            mode = SelectDataType.REMOTE
            showClearIcon = conf.showClearIcon
            editable = true
            multiple = false
            hasDownArrow = false
        }
        val button = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:link"
        }
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width = conf.width
            height = conf.height
            noPadding = true
            column("100%")
            column("auto")
            row {
                cell(webSelect)
                cell(button)
            }
        }
        webSelect.setLoader { value ->
            val request = AutocompleteRequestJS()
            request.autocompleteFieldName = conf.handler.getAutocompleteFieldName()
            request.listId = conf.handler.getIndexClassName()
            request.limit = 10
            request.pattern = value
            StandardRestClient.standard_standard_autocomplete(request).items.map {
                toSelectItem(it.document)
            }
        }
        button.setHandler {
            getValue()?.let {
                MainFrame.get().openTab(OpenObjectData(it.type, it.uid, null))
            }
        }
    }

    fun getValue():ObjectReferenceJS?{
        return webSelect.getValues().map { toObjectReference(it) }.firstOrNull()
    }

    fun setValue(value: ObjectReferenceJS?) {
        webSelect.setValues(if (value == null) emptyList() else arrayListOf(toSelectItem(value)))
    }


    fun configure(config: EntitySelectBoxConfigurationJS?){
        config?.let {
            webSelect.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        webSelect.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        webSelect.setEnabled(!value)
    }
    private fun toSelectItem(ref:ObjectReferenceJS): SelectItemJS {
        return SelectItemJS("${ref.type}||${ref.uid}", ref.caption?:"???")
    }
    private fun toObjectReference(item: SelectItemJS):ObjectReferenceJS{
        return ObjectReferenceJS(item.id.substringBefore("||"), item.id.substringAfter("||"), item.text)
    }


}

class EntitySelectWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
    lateinit var handler: AutocompleteHandler
}