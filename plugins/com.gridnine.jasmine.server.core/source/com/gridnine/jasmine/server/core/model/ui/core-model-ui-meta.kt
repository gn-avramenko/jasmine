/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseModelElementDescription


enum class RestPropertyType {

    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY

}

abstract class BaseUiElementDescription(val id:String)

class UiEnumItemDescription(id:String) :BaseModelElementDescription(id)

class UiEnumDescription(id:String) : BaseUiElementDescription(id){
    val items = linkedMapOf<String, UiEnumItemDescription>()
}


class UiMetaRegistry:Disposable{
    val enums = linkedMapOf<String, UiEnumDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(UiMetaRegistry::class)
        fun get() = wrapper.get()
    }
}


