/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.custom

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper

enum class CustomType {

    STRING,
    ENUM,
    ENTITY,
    LONG,
    CLASS,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY
}

abstract class BaseCustomElementDescription(val id:String)

class CustomPropertyDescription(id: String, val type:CustomType,val lateinit:Boolean,val nonNullable:Boolean):BaseCustomElementDescription(id){
    var className: String? = null
}

class CustomCollectionDescription(id: String, val elementType:CustomType):BaseCustomElementDescription(id){
    var elementClassName: String? = null
}

class CustomMapDescription(id: String, val keyClassType:CustomType, val valueClassType:CustomType):BaseCustomElementDescription(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}

class CustomEnumItemDescription(id:String) :BaseCustomElementDescription(id)

class CustomEnumDescription(id:String) : BaseCustomElementDescription(id){
    val items = linkedMapOf<String, CustomEnumItemDescription>()
}


class CustomEntityDescription(id:String) : BaseCustomElementDescription(id) {

    var abstract:Boolean = false

    var extends:String? = null

    val properties = linkedMapOf<String, CustomPropertyDescription>()

    val collections = linkedMapOf<String, CustomCollectionDescription>()

    val maps = linkedMapOf<String,CustomMapDescription>()
}


class CustomMetaRegistry:Disposable{
    val enums = linkedMapOf<String, CustomEnumDescription>()

    val entities = linkedMapOf<String, CustomEntityDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(CustomMetaRegistry::class)
        fun get() = wrapper.get()
    }
}

