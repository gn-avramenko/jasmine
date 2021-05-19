/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS


class UiEnumItemDescriptionJS(id:String) :BaseModelElementDescriptionJS(id)

class UiEnumDescriptionJS(id:String) : BaseModelElementDescriptionJS(id){
    val items = linkedMapOf<String, UiEnumItemDescriptionJS>()
}

enum class VMPropertyTypeJS {

    STRING,
    ENUM,
    SELECT,
    LONG,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    ENTITY,
    BOOLEAN,
}

enum class VMCollectionTypeJS {
    ENTITY
}

class VMCollectionDescriptionJS(id:String, val elementType:VMCollectionTypeJS, val elementClassName:String?) : BaseModelElementDescriptionJS(id)

class VMPropertyDescriptionJS(id:String, val type:VMPropertyTypeJS, val className:String?,val nonNullable: Boolean, val lateInit:Boolean) : BaseModelElementDescriptionJS(id)


class VMEntityDescriptionJS(id: String) : BaseModelElementDescriptionJS(id) {
    var extendsId:String? = null

    val properties = linkedMapOf<String, VMPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VMCollectionDescriptionJS>()
}

enum class VSPropertyTypeJS {
    STRING,
    ENTITY,
    TEXT_BOX_SETTINGS,
    PASSWORD_BOX_SETTINGS,
    FLOAT_NUMBER_BOX_SETTINGS,
    INTEGER_NUMBER_BOX_SETTINGS,
    BOOLEAN_BOX_SETTINGS,
    ENTITY_SELECT_BOX_SETTINGS,
    GENERAL_SELECT_BOX_SETTINGS,
    ENUM_SELECT_BOX_SETTINGS,
    DATE_BOX_SETTINGS,
    DATE_TIME_BOX_SETTINGS

}

enum class VSCollectionTypeJS {
    ENTITY
}

class VSCollectionDescriptionJS(id:String, val elementType:VSCollectionTypeJS, val elementClassName:String?) : BaseModelElementDescriptionJS( id)
class VSPropertyDescriptionJS(id:String, val type:VSPropertyTypeJS, val className:String?, val lateInit:Boolean) : BaseModelElementDescriptionJS(id)
class VSEntityDescriptionJS(id: String) : BaseModelElementDescriptionJS(id) {

    var extendsId:String? = null

    val properties = linkedMapOf<String, VSPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VSCollectionDescriptionJS>()
}


enum class VVPropertyTypeJS {
    STRING,
    ENTITY
}

enum class VVCollectionTypeJS {
    ENTITY
}

class VVCollectionDescriptionJS(id:String, val elementType:VVCollectionTypeJS, val elementClassName:String?) : BaseModelElementDescriptionJS( id)
class VVPropertyDescriptionJS(id:String, val type:VVPropertyTypeJS, val className:String?, val lateInit:Boolean) : BaseModelElementDescriptionJS(id)
class VVEntityDescriptionJS(id: String) : BaseModelElementDescriptionJS(id) {

    var extendsId:String? = null

    val properties = linkedMapOf<String, VVPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VVCollectionDescriptionJS>()
}

open class OptionDescriptionJS(id:String) :BaseModelElementDescriptionJS(id)
class OptionsGroupDescriptionJS(id:String) :BaseModelElementDescriptionJS(id){
    val options = arrayListOf<OptionDescriptionJS>()
}

class UiMetaRegistryJS {
    val enums = linkedMapOf<String, UiEnumDescriptionJS>()

    val viewModels = linkedMapOf<String, VMEntityDescriptionJS>()

    val viewSettings = linkedMapOf<String, VSEntityDescriptionJS>()

    val viewValidations = linkedMapOf<String, VVEntityDescriptionJS>()

    val optionsGroups  = linkedMapOf<String, OptionsGroupDescriptionJS>()
    companion object {
        fun get() = EnvironmentJS.getPublished(UiMetaRegistryJS::class)
    }
}


