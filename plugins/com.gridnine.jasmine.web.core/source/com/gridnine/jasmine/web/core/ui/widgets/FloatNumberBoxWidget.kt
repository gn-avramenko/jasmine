/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox
import com.gridnine.jasmine.web.core.ui.components.WebNumberBoxConfiguration
import kotlin.js.Date

class FloatNumberBoxWidget(parent:WebComponent, configure:FloatNumberBoxWidgetConfiguration.()->Unit
                           , private val delegate:WebNumberBox = UiLibraryAdapter.get().createNumberBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{

    fun setValue(value:Double?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

    companion object{
        private fun convertConfiguration(configure: FloatNumberBoxWidgetConfiguration.() -> Unit): WebNumberBoxConfiguration.()->Unit {
            val conf = FloatNumberBoxWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                showClearIcon = conf.showClearIcon
                precision = conf.precision
            }
        }
    }
}





class FloatNumberBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
    var precision = 2
}