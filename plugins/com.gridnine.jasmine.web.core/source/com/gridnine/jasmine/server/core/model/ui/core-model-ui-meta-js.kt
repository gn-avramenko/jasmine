/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.BaseMetaElementDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.AssetDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.DocumentDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.DomainEnumDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.IndexDescriptionJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS

class UiEnumItemDescriptionJS(id:String,val displayName:String) : BaseMetaElementDescriptionJS(id)


class UiEnumDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id){
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

class VMCollectionDescriptionJS(id:String, val elementType:VMCollectionTypeJS, val elementClassName:String?) : BaseMetaElementDescriptionJS(id)

class VMPropertyDescriptionJS(id:String, val type:VMPropertyTypeJS, val className:String?,val nonNullable: Boolean) : BaseMetaElementDescriptionJS( id)


class VMEntityDescriptionJS(id: String) : BaseMetaElementDescriptionJS(id) {

    val properties = linkedMapOf<String, VMPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VMCollectionDescriptionJS>()
}

enum class VSPropertyTypeJS {
    TEXT_BOX_SETTINGS,
    PASSWORD_BOX_SETTINGS,
    ENTITY,
    FLOAT_NUMBER_BOX_SETTINGS,
    INTEGER_NUMBER_BOX_SETTINGS,
    BOOLEAN_BOX_SETTINGS,
    ENTITY_SELECT_BOX_SETTINGS,
    ENUM_SELECT_BOX_SETTINGS,
    DATE_BOX_SETTINGS,
    DATE_TIME_BOX_SETTINGS
}

enum class VSCollectionTypeJS {
    ENTITY
}

class VSCollectionDescriptionJS(id:String, val elementType:VSCollectionTypeJS, val elementClassName:String?) : BaseMetaElementDescriptionJS( id)
class VSPropertyDescriptionJS(id:String, val type:VSPropertyTypeJS, val className:String?) : BaseMetaElementDescriptionJS(id)
class VSEntityDescriptionJS(id: String) : BaseMetaElementDescriptionJS(id) {

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

class VVCollectionDescriptionJS(id:String, val elementType:VVCollectionTypeJS, val elementClassName:String?) : BaseMetaElementDescriptionJS( id)
class VVPropertyDescriptionJS(id:String, val type:VVPropertyTypeJS, val className:String?) : BaseMetaElementDescriptionJS(id)
class VVEntityDescriptionJS(id: String) : BaseMetaElementDescriptionJS(id) {

    val properties = linkedMapOf<String, VVPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VVCollectionDescriptionJS>()
}

class UiMetaRegistryJS{
    val enums = linkedMapOf<String, UiEnumDescriptionJS>()

    val viewModels = linkedMapOf<String, VMEntityDescriptionJS>()

    val viewSettings = linkedMapOf<String, VSEntityDescriptionJS>()

    val viewValidations = linkedMapOf<String, VVEntityDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(UiMetaRegistryJS::class)
    }
}