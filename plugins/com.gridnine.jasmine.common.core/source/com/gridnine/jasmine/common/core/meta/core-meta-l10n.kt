/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper

enum class L10nMessageParameterType {
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
class L10nMessageParameterDescription(id:String, val type: L10nMessageParameterType) : BaseModelElementDescription(id){
    var className:String?=null
    var collection:Boolean = false
}

class L10nMessageDescription(id:String) : BaseModelElementDescription(id){
    val params = linkedMapOf<String, L10nMessageParameterDescription>()
}

class L10nMessagesBundleDescription(val id:String){
    val messages = linkedMapOf<String, L10nMessageDescription>()
}

class L10nMetaRegistry: Disposable {
    val bundles = linkedMapOf<String, L10nMessagesBundleDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(L10nMetaRegistry::class)
        fun get() = wrapper.get()
    }
}



