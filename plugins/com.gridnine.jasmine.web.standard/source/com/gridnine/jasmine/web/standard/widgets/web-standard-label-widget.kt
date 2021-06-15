/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS


class WebLabelWidget(text:String? = null, configure:(WebLabelWidgetConfiguration.()->Unit)? = null):BaseWebNodeWrapper<WebTag>(){
    init {
        val config = WebLabelWidgetConfiguration()
        if(configure != null){
            config.configure()
        }
        _node = WebUiLibraryAdapter.get().createTag("div", "label:${MiscUtilsJS.createUUID()}")
        _node.getStyle().setParameters("display" to "inline-block")
        config.className?.let {_node.getClass().addClasses(it)}
        config.width?.let { _node.getStyle().setParameters("width" to it) }
        config.height?.let { _node.getStyle().setParameters("height" to it) }
        _node.setText(text)
    }

    fun setText(text:String?){
        _node.setText(text)
    }
}

class WebLabelWidgetConfiguration:BaseWidgetConfiguration(){
    var className:String? = null
}