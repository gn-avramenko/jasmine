/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused","UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseModelElementDescription

enum class ServerMessageParameterType {
    STRING,
    LOCAL_DATE,
    LOCAL_DATE_TIME,
    ENUM,
    BOOLEAN,
    ENTITY_REFERENCE,
    LONG,
    INT,
    BIG_DECIMAL
}
class ServerMessageParameterDescription(id:String, val type:ServerMessageParameterType) : BaseModelElementDescription(id){
    var className:String?=null
    var collection:Boolean = false
}

class ServerMessageDescription(id:String) : BaseModelElementDescription(id){
    val params = linkedMapOf<String, ServerMessageParameterDescription>()
}

class ServerMessagesBundleDescription(val id:String, val factoryClassName:String){
    val messages = linkedMapOf<String, ServerMessageDescription>()
}

class WebMessagesBundleDescription(val id:String, val messagesClassName:String){
    val messages = linkedMapOf<String, WebMessageDescription>()
}

class WebMessageDescription(id:String) : BaseModelElementDescription(id)

class L10nMetaRegistry:Disposable{
    val serverMessages = linkedMapOf<String, ServerMessagesBundleDescription>()

    val webMessages = linkedMapOf<String, WebMessagesBundleDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(L10nMetaRegistry::class)
        fun get() = wrapper.get()
    }
}



