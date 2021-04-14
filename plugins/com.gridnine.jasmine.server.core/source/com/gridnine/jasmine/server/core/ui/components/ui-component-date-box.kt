/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode
import java.time.LocalDate

interface DateBox: UiNode {
    fun getValue():LocalDate?
    fun setValue(value:LocalDate?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class DateBoxComponentConfiguration: BaseComponentConfiguration()