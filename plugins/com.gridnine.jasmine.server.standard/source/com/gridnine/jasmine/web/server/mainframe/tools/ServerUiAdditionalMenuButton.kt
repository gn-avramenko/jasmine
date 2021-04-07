/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.tools

import com.gridnine.jasmine.web.server.common.ServerUiMainFrameMenuButton

class ServerUiAdditionalMenuButton :ServerUiMainFrameMenuButton{
    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return "Дополнительно"
    }

    override fun getId(): String {
        return ID
    }

    override fun getWeight(): Double {
        return 20.0
    }

    companion object{
        const val ID = "ServerUiAdditionalMenuButton"
    }
}