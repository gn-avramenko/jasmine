/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfigurationJS
import com.gridnine.jasmine.server.core.model.ui.DateTimeBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import kotlin.js.Date

class DateTimeBoxWidget(aParent:WebComponent, configure:DateTimeBoxWidgetConfiguration.()->Unit):WebComponent{

    private val delegate:WebDateTimeBox
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = DateTimeBoxWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createDateTimeBox(this){
            width = conf.width
            height = conf.height
            showClearIcon = conf.showClearIcon
        }
    }

    fun setValue(value:Date?) = delegate.setValue(value)

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

    fun configure(config: DateTimeBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        delegate.showValidation(value)
    }
}





class DateTimeBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}