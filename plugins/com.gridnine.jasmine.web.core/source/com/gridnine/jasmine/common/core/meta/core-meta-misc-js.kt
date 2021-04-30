/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS


enum class MiscFieldTypeJS {

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

abstract class BaseMiscElementDescriptionJS(id:String):BaseModelElementDescriptionJS(id)

class MiscPropertyDescriptionJS(id: String, val type: MiscFieldTypeJS, val lateinit:Boolean, val nonNullable:Boolean): BaseMiscElementDescriptionJS(id){
    var className: String? = null
}

class MiscCollectionDescriptionJS(id: String, val elementType: MiscFieldTypeJS): BaseMiscElementDescriptionJS(id){
    var elementClassName: String? = null
}


class MiscMapDescriptionJS(id: String, val keyClassType: MiscFieldTypeJS, val valueClassType: MiscFieldTypeJS): BaseMiscElementDescriptionJS(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}

class MiscEnumItemDescriptionJS(id:String) : BaseMiscElementDescriptionJS(id)

class MiscEnumDescriptionJS(id:String) : BaseMiscElementDescriptionJS(id){
    val items = linkedMapOf<String, MiscEnumItemDescriptionJS>()
}


class MiscEntityDescriptionJS(id:String) : BaseMiscElementDescriptionJS(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = linkedMapOf<String, MiscPropertyDescriptionJS>()

    val collections = linkedMapOf<String, MiscCollectionDescriptionJS>()

    val maps = linkedMapOf<String, MiscMapDescriptionJS>()
}


class MiscMetaRegistryJS{
    val enums = linkedMapOf<String, MiscEnumDescriptionJS>()

    val entities = linkedMapOf<String, MiscEntityDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(MiscMetaRegistryJS::class)
    }
}

