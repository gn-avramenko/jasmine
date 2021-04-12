/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.common.core.model.L10nMessage


object CoreServerL10nMessagesFactory {
    const val bundle = "core"

    fun FOUND_SEVERAL_RECORDS(objectType:String?, propertyName:String?, propertyValue:String?) = L10nMessage(bundle, "FOUND_SEVERAL_RECORDS", objectType?:"???"
            , propertyName?:"???", propertyValue?:"???")
}