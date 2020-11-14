/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox
import com.gridnine.jasmine.web.core.ui.components.WebNumberBoxConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import kotlin.js.Date

class IntegerNumberBoxWidget(private val parent:WebComponent, configure:IntegerNumberBoxWidgetConfiguration.()->Unit):WebComponent{
    private val delegate: WebNumberBox
    private val conf = IntegerNumberBoxWidgetConfiguration()
    init {
        conf.configure()
        delegate = UiLibraryAdapter.get().createNumberBox(this){
            width = conf.width
            height = conf.height
            showClearIcon = !conf.nullable && conf.showClearIcon
            precision = 0
        }
    }
    fun setValue(value:Int?) = delegate.setValue(value?.toDouble())

    fun getValue() = delegate.getValue()?.toInt()?:(if(conf.nullable) null else 0)
    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun destroy() {
        delegate.destroy()
    }

    fun configure(config:IntegerNumberBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        delegate.showValidation(value)
    }
}





class IntegerNumberBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var nullable = true
    var showClearIcon = true
}