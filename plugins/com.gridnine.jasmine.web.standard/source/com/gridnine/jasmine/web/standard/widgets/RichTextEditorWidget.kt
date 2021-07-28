/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.RichTextEditorConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebRichTextEditor

class RichTextEditorWidget(configure:RichTextEditorWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebRichTextEditor>(){

    private val config = RichTextEditorWidgetConfiguration()

    private var conf:RichTextEditorConfigurationJS? = null
    private var readonly = false
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createRichTextEditor {
            width = config.width
            height = config.height?:"100%"
        }
    }

    fun setValue(value:String?) = _node.setContent(value)

    fun getValue() = _node.getContent()

    fun setReadonly(value:Boolean) {
        readonly = value
        updateDisabledMode()
    }

    private fun updateDisabledMode() {
        _node.setDisabled(conf?.notEditable == true || readonly)
    }


    fun showValidation(value: String?) {
        //noops
    }

    fun configure(config: RichTextEditorConfigurationJS) {
        this.conf = config
        updateDisabledMode()
    }

}


class RichTextEditorWidgetConfiguration:BaseWidgetConfiguration(){}
