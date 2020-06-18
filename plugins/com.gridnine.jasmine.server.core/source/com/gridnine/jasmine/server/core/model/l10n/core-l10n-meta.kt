/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused","UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseModelElementDescription

enum class L10nParameterType {
    STRING,
    LOCAL_DATE,
    LOCAL_DATE_TIME,
    ENUM,
    BOOLEAN,
    BYTE_ARRAY,
    NESTED_DOCUMENT,
    ENTITY_REFERENCE,
    LONG,
    INT,
    BIG_DECIMAL
}
class L10nParameterDescription(id:String, val type:L10nParameterType) : BaseModelElementDescription(id){
    var className:String?=null
    var collection:Boolean = false
}

class L10nMessageDescription(id:String) : BaseModelElementDescription(id){
    val params = linkedMapOf<String, L10nParameterDescription>()
}

class L10nMetaregistry:Disposable{
    val messages = linkedMapOf<String, L10nMessageDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(L10nMetaregistry::class)
        fun get() = wrapper.get()
    }
}



