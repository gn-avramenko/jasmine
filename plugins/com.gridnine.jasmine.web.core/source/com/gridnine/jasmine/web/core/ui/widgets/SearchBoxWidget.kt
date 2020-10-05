/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebSearchBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBoxConfiguration

class SearchBoxWidget(parent:WebComponent, configure:SearchBoxWidgetConfiguration.()->Unit
                      , private val delegate:WebSearchBox = UiLibraryAdapter.get().createSearchBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{

    fun setSearcher(value: (String?) ->Unit){
        delegate.setSearcher(value)
    }

    fun getValue() = delegate.getValue()

    companion object{
        private fun convertConfiguration(configure: SearchBoxWidgetConfiguration.() -> Unit): WebSearchBoxConfiguration.()->Unit {
            val conf = SearchBoxWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                prompt = conf.prompt
            }
        }
    }
}





class SearchBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
}