/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.common.core.model.L10nMessage


object CoreServerL10nMessagesFactory {
    const val bundle = "core"

    fun Found_several_records(objectType:String?, propertyName:String?, propertyValue:String?) = Found_several_recordsMessage(objectType, propertyName, propertyValue).toString()

    fun Found_several_recordsMessage(objectType:String?, propertyName:String?, propertyValue:String?) = L10nMessage(bundle, "Found_several_records", objectType?:"???"
            , propertyName?:"???", propertyValue?:"???")

    fun Yes() = YesMessage().toString()

    fun YesMessage() = L10nMessage(bundle, "Yes")

    fun No() = NoMessage().toString()

    fun NoMessage() = L10nMessage(bundle, "No")

    fun Choose_variant() = Choose_variantMessage().toString()

    fun Choose_variantMessage() = L10nMessage(bundle, "Choose_variant")

    fun Question() = QuestionMessage().toString()

    fun QuestionMessage() = L10nMessage(bundle, "Question")

    fun Object_not_found(objectId:String?, objectUid:String?) = Object_not_foundMessage(objectId, objectUid).toString()

    fun Object_not_foundMessage(objectId:String?, objectUid:String?) = L10nMessage(bundle, "Object_not_found", objectId?:"???"
            , objectUid?:"???")

}