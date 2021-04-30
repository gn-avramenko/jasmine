/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.l10n

import com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS

object WebCoreL10nMessagesInitializer {
    fun initialize(){
        val messages = L10nMetaRegistryJS.get().messages
        WebCoreL10nMessages.Unknown_error = messages["core"]!!["Unknown_error"]!!
    }
}