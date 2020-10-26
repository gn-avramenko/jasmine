/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateBox
import kotlin.js.Date

class DateBoxWidget(aParent:WebComponent, configure:DateBoxWidgetConfiguration.()->Unit):WebComponent{

    private val delegate:WebDateBox
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = DateBoxWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createDateBox(this){
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

}





class DateBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}