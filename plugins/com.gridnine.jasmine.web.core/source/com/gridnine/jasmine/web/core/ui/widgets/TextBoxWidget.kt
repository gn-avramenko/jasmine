/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebTextBox
import com.gridnine.jasmine.web.core.ui.components.WebTextBoxConfiguration

class TextBoxWidget(parent:WebComponent, configure:TextBoxWidgetConfiguration.()->Unit
                      , private val delegate:WebTextBox = UiLibraryAdapter.get().createTextBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{

    fun setValue(value:String?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

    companion object{
        private fun convertConfiguration(configure: TextBoxWidgetConfiguration.() -> Unit): WebTextBoxConfiguration.()->Unit {
            val conf = TextBoxWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                prompt = conf.prompt
                showClearIcon = conf.showClearIcon
            }
        }
    }
}





class TextBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
    var showClearIcon = true
}