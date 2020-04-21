/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.server.core.model.common.L10nMessage

object CoreL10nMessagesFactory {
    fun FOUND_SEVERAL_RECORDS(objectType:String?, propertyName:String?, propertyValue:String?) = L10nMessage("FOUND_SEVERAL_RECORDS", objectType?:"???"
            , propertyName?:"???", propertyValue?:"???")
}