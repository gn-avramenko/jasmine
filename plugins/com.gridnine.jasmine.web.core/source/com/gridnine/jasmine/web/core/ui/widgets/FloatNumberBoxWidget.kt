/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfigurationJS
import com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxConfigurationJS
import com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox
import com.gridnine.jasmine.web.core.ui.components.WebNumberBoxConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import kotlin.js.Date

class FloatNumberBoxWidget(aParent:WebComponent, configure:FloatNumberBoxWidgetConfiguration.()->Unit):WebComponent{
    private val delegate: WebNumberBox
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = FloatNumberBoxWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createNumberBox(this){
            width = conf.width
            height = conf.height
            showClearIcon = conf.showClearIcon
            precision = conf.precision
        }
    }
    fun setValue(value:Double?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()
    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return children
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

    fun setReadonly(value:Boolean) {
        delegate.setEnabled(!value)
    }

    fun configure(config: FloatNumberBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        delegate.showValidation(value)
    }
}





class FloatNumberBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
    var precision = 2
}