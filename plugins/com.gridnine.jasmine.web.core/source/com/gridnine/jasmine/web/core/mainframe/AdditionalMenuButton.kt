/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.ui.MenuButton

class AdditionalMenuButton : MenuButton{
    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return CoreWebMessagesJS.more
    }

    override fun getWeight(): Double {
        return 100.0;
    }

    override fun getId(): String {
        return id
    }

    companion object{
        val id = "AdditionalMenuButton"
    }

}