/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.server.core.model.common.ServerMessage

object CoreServerMessagesFactory {
    const val bundle = "core"

    fun FOUND_SEVERAL_RECORDS(objectType:String?, propertyName:String?, propertyValue:String?) = ServerMessage(bundle, "FOUND_SEVERAL_RECORDS", objectType?:"???"
            , propertyName?:"???", propertyValue?:"???")
}