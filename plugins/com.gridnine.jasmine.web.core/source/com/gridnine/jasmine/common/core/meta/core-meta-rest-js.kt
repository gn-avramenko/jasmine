/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS


enum class RestPropertyTypeJS {

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

abstract class BaseRestElementDescriptionJS(val id:String)

class RestPropertyDescriptionJS(id: String, val type:RestPropertyTypeJS,val lateinit:Boolean,val nonNullable:Boolean):BaseRestElementDescriptionJS(id){
    var className: String? = null
}

class RestCollectionDescriptionJS(id: String, val elementType:RestPropertyTypeJS):BaseRestElementDescriptionJS(id){
    var elementClassName: String? = null
}

class RestEnumItemDescriptionJS(id:String) :BaseRestElementDescriptionJS(id)

class RestEnumDescriptionJS(id:String) : BaseRestElementDescriptionJS(id){
    val items = linkedMapOf<String, RestEnumItemDescriptionJS>()
}

class RestGroupDescriptionJS(id:String, val restId:String) : BaseRestElementDescriptionJS(id)

class RestDescriptionJS(id:String) : BaseRestElementDescriptionJS(id)

class RestOperationDescriptionJS(id:String, val groupId:String, val requestEntity:String, val responseEntity:String) : BaseRestElementDescriptionJS(id)

class RestMapDescriptionJS(id: String, val keyClassType: RestPropertyTypeJS, val valueClassType: RestPropertyTypeJS): BaseModelElementDescriptionJS(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}


class RestEntityDescriptionJS(id:String) : BaseRestElementDescriptionJS(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = LinkedHashMap<String, RestPropertyDescriptionJS>()

    val collections = LinkedHashMap<String, RestCollectionDescriptionJS>()

    val maps = LinkedHashMap<String, RestMapDescriptionJS>()
}


class RestMetaRegistryJS {
    val enums = linkedMapOf<String, RestEnumDescriptionJS>()

    val entities = linkedMapOf<String, RestEntityDescriptionJS>()

    val rests = linkedMapOf<String, RestDescriptionJS>()

    val groups = linkedMapOf<String, RestGroupDescriptionJS>()

    val operations = linkedMapOf<String, RestOperationDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(RestMetaRegistryJS::class)
    }
}


