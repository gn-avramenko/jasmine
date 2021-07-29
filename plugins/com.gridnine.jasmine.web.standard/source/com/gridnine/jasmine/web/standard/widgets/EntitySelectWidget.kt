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
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.mainframe.MainFrame

class EntitySelectWidget(configure:EntitySelectWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebGridLayoutWidget>() {
    private val webSelect:WebSelect

    private val config = EntitySelectWidgetConfiguration()

    private var conf: EntitySelectBoxConfigurationJS? = null

    private var readonly = false
    init {
        config.configure()
        webSelect =WebUiLibraryAdapter.get().createSelect{
            width = "100%"
            height = "100%"
            mode = SelectDataType.REMOTE
            showClearIcon = config.showClearIcon
            editable = true
            multiple = false
            hasDownArrow = false
        }
        if(config.showLinkButton) {
            val button = WebUiLibraryAdapter.get().createLinkButton {
                icon = "core:link"
            }
            _node = WebGridLayoutWidget {
                width = config.width
                height = config.height
                noPadding = true
            }.also {
                it.setColumnsWidths("100%", "auto")
                it.addRow(webSelect, button)
            }
            webSelect.setLoader { value ->
                val request = AutocompleteRequestJS()
                request.autocompleteFieldName = config.handler.getAutocompleteFieldName()
                request.listId = config.handler.getIndexClassName()
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
        } else {
            _node = WebGridLayoutWidget {
                width = config.width
                height = config.height
                noPadding = true
            }.also {
                it.setColumnsWidths("100%")
                it.addRow(webSelect)
            }
            webSelect.setLoader { value ->
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
    }

    fun getValue():ObjectReferenceJS?{
        return webSelect.getValues().map { toObjectReference(it) }.firstOrNull()
    }

    fun setValue(value: ObjectReferenceJS?) {
        webSelect.setValues(if (value == null) emptyList() else arrayListOf(toSelectItem(value)))
    }


    fun configure(config: EntitySelectBoxConfigurationJS?){
        conf = config
        updateEnabledMode()
    }

    fun showValidation(value:String?){
        webSelect.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        readonly =value
        updateEnabledMode()
    }

    private fun updateEnabledMode() {
        webSelect.setEnabled(!((config.notEditable && conf?.notEditable != false) || conf?.notEditable == true || readonly))
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
    var showLinkButton = true
}