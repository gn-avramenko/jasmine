/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfigurationJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebBooleanBox

class BooleanBoxWidget(private val parent:WebComponent, configure:BooleanBoxWidgetConfiguration.()->Unit):WebComponent{
    private val delegate: WebBooleanBox
    private val conf = BooleanBoxWidgetConfiguration()
    init {
        conf.configure()
        delegate = UiLibraryAdapter.get().createBooleanBox(this){
            width = conf.width
            height = conf.height
            offText = CoreWebMessagesJS.NO
            onText = CoreWebMessagesJS.YES

        }
    }
    fun setValue(value:Boolean) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

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

    fun setReadonly(value:Boolean) {
        delegate.setEnabled(!value)
    }

    fun configure(config:BooleanBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        //noops
    }
}


class BooleanBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
}