/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.LongNumberBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox

class LongNumberBoxWidget(private val parent:WebComponent, configure:LongNumberBoxWidgetConfiguration.()->Unit):WebComponent{
    private val delegate: WebNumberBox
    private val conf = LongNumberBoxWidgetConfiguration()
    init {
        conf.configure()
        delegate = UiLibraryAdapter.get().createNumberBox(this){
            width = conf.width
            height = conf.height
            showClearIcon = conf.nullable && conf.showClearIcon
            precision = 0
        }
    }
    fun setValue(value:Long?) = delegate.setValue(value?.toDouble())

    fun getValue() = delegate.getValue()?.toLong()?:(if(conf.nullable) null else 0) as Long?
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

    fun configure(config: LongNumberBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        delegate.showValidation(value)
    }
}





class LongNumberBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var nullable = true
    var showClearIcon = true
}