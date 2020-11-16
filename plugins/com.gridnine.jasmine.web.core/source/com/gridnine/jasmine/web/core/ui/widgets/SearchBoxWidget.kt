/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBoxConfiguration

class SearchBoxWidget(aParent:WebComponent, configure:SearchBoxWidgetConfiguration.()->Unit):WebComponent{

    private val delegate: WebSearchBox
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = SearchBoxWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createSearchBox(aParent) {
            width = conf.width
            height = conf.height
            prompt = conf.prompt
        }
    }
    fun setSearcher(value: (String?) ->Unit){
        delegate.setSearcher(value)
    }

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

    fun configure(config: BooleanBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }
}





class SearchBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
}