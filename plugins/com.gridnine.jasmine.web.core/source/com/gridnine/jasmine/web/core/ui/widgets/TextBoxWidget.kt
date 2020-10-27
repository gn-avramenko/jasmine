/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import com.gridnine.jasmine.web.core.ui.components.WebTextBox
import com.gridnine.jasmine.web.core.ui.components.WebTextBoxConfiguration

class TextBoxWidget(aParent:WebComponent, configure:TextBoxWidgetConfiguration.()->Unit):WebComponent{

    private val delegate: WebTextBox
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = TextBoxWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createTextBox(this) {
            width = conf.width
            height = conf.height
            prompt = conf.prompt
            showClearIcon = conf.showClearIcon
        }
    }

    fun setValue(value:String?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

    fun setReadonly(value:Boolean) = delegate.setDisabled(value)
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

    fun showValidationError(value: String) {
        delegate.showValidation(value)
    }

    fun resetValidation(){
        delegate.resetValidation()
    }

}


class TextBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
    var showClearIcon = true
}