/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode
import java.time.LocalDateTime

interface DateTimeBox: UiNode {
    fun getValue():LocalDateTime?
    fun setValue(value:LocalDateTime?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class DateTimeBoxComponentConfiguration: BaseComponentConfiguration()