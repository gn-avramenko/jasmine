/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS

enum class CustomTypeJS {

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

abstract class BaseCustomElementDescriptionJS(val id:String)

class CustomPropertyDescriptionJS(id: String, val type: CustomTypeJS, val lateinit:Boolean, val nonNullable:Boolean): BaseCustomElementDescriptionJS(id){
    var className: String? = null
}

class CustomCollectionDescriptionJS(id: String, val elementType: CustomTypeJS): BaseCustomElementDescriptionJS(id){
    var elementClassName: String? = null
}

class CustomMapDescriptionJS(id: String, val keyClassType: CustomTypeJS, val valueClassType: CustomTypeJS): BaseCustomElementDescriptionJS(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}

class CustomEnumItemDescriptionJS(id:String) : BaseCustomElementDescriptionJS(id)

class CustomEnumDescriptionJS(id:String) : BaseCustomElementDescriptionJS(id){
    val items = linkedMapOf<String, CustomEnumItemDescriptionJS>()
}


class CustomEntityDescriptionJS(id:String) : BaseCustomElementDescriptionJS(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = linkedMapOf<String, CustomPropertyDescriptionJS>()

    val collections = linkedMapOf<String, CustomCollectionDescriptionJS>()

    val maps = linkedMapOf<String, CustomMapDescriptionJS>()
}


class CustomMetaRegistryJS {
    val enums = linkedMapOf<String, CustomEnumDescriptionJS>()

    val entities = linkedMapOf<String, CustomEntityDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(CustomMetaRegistryJS::class)
    }
}

