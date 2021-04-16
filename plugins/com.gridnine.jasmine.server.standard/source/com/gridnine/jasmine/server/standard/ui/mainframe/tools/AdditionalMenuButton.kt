/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.tools

import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.MainFrameMenuButton

class AdditionalMenuButton :MainFrameMenuButton{
    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return StandardL10nMessagesFactory.more()
    }

    override fun getId(): String {
        return ID
    }

    override fun getWeight(): Double {
        return 20.0
    }

    companion object{
        const val ID = "AdditionalMenuButton"
    }
}