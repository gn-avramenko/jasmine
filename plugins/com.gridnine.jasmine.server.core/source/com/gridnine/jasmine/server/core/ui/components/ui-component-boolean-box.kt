/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode


interface BooleanBox: UiNode {
    fun getValue():Boolean
    fun setValue(value:Boolean)
    fun setEnabled(value:Boolean)
}

class BooleanBoxComponentConfiguration: BaseComponentConfiguration()