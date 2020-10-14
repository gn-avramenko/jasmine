/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDateBox
import com.gridnine.jasmine.web.core.ui.components.WebDateBoxConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBoxConfiguration
import kotlin.js.Date

class DateTimeBoxWidget(parent:WebComponent, configure:DateTimeBoxWidgetConfiguration.()->Unit
                      , private val delegate:WebDateTimeBox = UiLibraryAdapter.get().createDateTimeBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{

    fun setValue(value:Date?) = delegate.setValue(value)

    fun getValue() = delegate.getValue()

    companion object{
        private fun convertConfiguration(configure: DateTimeBoxWidgetConfiguration.() -> Unit): WebDateTimeBoxConfiguration.()->Unit {
            val conf = DateTimeBoxWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                showClearIcon = conf.showClearIcon
                showSeconds = false
            }
        }
    }
}





class DateTimeBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}