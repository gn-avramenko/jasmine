/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode
import java.math.BigDecimal

interface NumberBox: UiNode {
    fun getValue():BigDecimal?
    fun setValue(value:BigDecimal?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class NumberBoxComponentConfiguration: BaseComponentConfiguration(){
    var precision = 2
}