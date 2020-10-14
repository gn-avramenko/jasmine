/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateBox
import com.gridnine.jasmine.web.core.ui.components.WebDateBoxConfiguration
import kotlin.js.Date

class DateBoxWidget(parent:WebComponent, configure:DateBoxWidgetConfiguration.()->Unit
                      , private val delegate:WebDateBox = UiLibraryAdapter.get().createDateBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{

    fun setValue(value:Date?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

    companion object{
        private fun convertConfiguration(configure: DateBoxWidgetConfiguration.() -> Unit): WebDateBoxConfiguration.()->Unit {
            val conf = DateBoxWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                showClearIcon = conf.showClearIcon
            }
        }
    }
}





class DateBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}