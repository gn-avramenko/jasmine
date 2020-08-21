/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.rest

import com.gridnine.jasmine.server.core.model.common.BaseMetaElementDescriptionJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS


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



class RestPropertyDescriptionJS(id: String, val type:RestPropertyTypeJS,val lateinit:Boolean,val nonNullable:Boolean,val className:String?):BaseMetaElementDescriptionJS(id)

class RestCollectionDescriptionJS(id: String, val elementType:RestPropertyTypeJS,val elementClassName: String?):BaseMetaElementDescriptionJS(id)

class RestEnumItemDescriptionJS(id:String) :BaseMetaElementDescriptionJS(id)

class RestEnumDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id){
    val items = linkedMapOf<String, RestEnumItemDescriptionJS>()
}

class RestGroupDescriptionJS(id:String, val restId:String) : BaseMetaElementDescriptionJS(id)

class RestDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id)

class RestOperationDescriptionJS(id:String, val requestEntity:String, val responseEntity:String) : BaseMetaElementDescriptionJS(id)




class RestEntityDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = LinkedHashMap<String, RestPropertyDescriptionJS>()

    val collections = LinkedHashMap<String, RestCollectionDescriptionJS>()

}


class RestMetaRegistryJS{
    val enums = linkedMapOf<String, RestEnumDescriptionJS>()

    val entities = linkedMapOf<String, RestEntityDescriptionJS>()

    val rests = linkedMapOf<String, RestDescriptionJS>()

    val groups = linkedMapOf<String, RestGroupDescriptionJS>()

    val operations = linkedMapOf<String, RestOperationDescriptionJS>()

    companion object {
        fun get(): RestMetaRegistryJS {
            return EnvironmentJS.getPublished(RestMetaRegistryJS::class)
        }
    }
}


