/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.rest

import com.gridnine.jasmine.server.core.app.Environment


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

abstract class BaseRestElementDescription(val id:String)

class RestPropertyDescription(id: String, val type:RestPropertyType,val lateinit:Boolean,val nonNullable:Boolean):BaseRestElementDescription(id){
    var className: String? = null
}

class RestCollectionDescription(id: String, val elementType:RestPropertyType):BaseRestElementDescription(id){
    var elementClassName: String? = null
}

class RestEnumItemDescription(id:String) :BaseRestElementDescription(id)

class RestEnumDescription(id:String) : BaseRestElementDescription(id){
    val items = linkedMapOf<String, RestEnumItemDescription>()
}

class RestGroupDescription(id:String, val restId:String) : BaseRestElementDescription(id)

class RestDescription(id:String) : BaseRestElementDescription(id)

class RestOperationDescription(id:String, val groupId:String, val requestEntity:String, val responseEntity:String, val handler:String) : BaseRestElementDescription(id)




class RestEntityDescription(id:String) : BaseRestElementDescription(id) {

    var abstract:Boolean = false

    var extends:String? = null

    val properties = LinkedHashMap<String, RestPropertyDescription>()

    val collections = LinkedHashMap<String, RestCollectionDescription>()

}


class RestMetaRegistry{
    val enums = linkedMapOf<String, RestEnumDescription>()

    val entities = linkedMapOf<String, RestEntityDescription>()

    val rests = linkedMapOf<String, RestDescription>()

    val groups = linkedMapOf<String, RestGroupDescription>()

    val operations = linkedMapOf<String, RestOperationDescription>()

    companion object {
        fun get(): RestMetaRegistry {
            return Environment.getPublished(RestMetaRegistry::class)
        }
    }
}


