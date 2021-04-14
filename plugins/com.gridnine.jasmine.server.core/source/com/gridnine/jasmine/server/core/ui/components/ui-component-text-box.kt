/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface TextBox: UiNode {
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
    fun setActionListener(listener: (String?) -> Unit)
    fun setDisabled(value:Boolean)
}

class TextBoxComponentConfiguration: BaseComponentConfiguration()