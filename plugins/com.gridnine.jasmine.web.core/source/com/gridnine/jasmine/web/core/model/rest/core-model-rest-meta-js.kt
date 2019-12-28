/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.web.core.model.rest

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.common.BaseIdentityDescriptionJS


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


class RestPropertyDescriptionJS(id: String, val type:RestPropertyTypeJS, val className:String?):BaseIdentityDescriptionJS(id)

class RestCollectionDescriptionJS(id: String, val elementType:RestPropertyTypeJS,val elementClassName:String?):BaseIdentityDescriptionJS(id)

class RestEnumItemDescriptionJS(id:String) :BaseIdentityDescriptionJS(id)

class RestEnumDescriptionJS(id:String) : BaseIdentityDescriptionJS(id){
    val items = linkedMapOf<String, RestEnumItemDescriptionJS>()
}


class RestOperationDescriptionJS(id:String,  val requestEntity:String, val responseEntity:String) : BaseIdentityDescriptionJS(id)




class RestEntityDescriptionJS(id:String) : BaseIdentityDescriptionJS(id) {

    var abstract = false

    var extends:String? = null

    val properties = linkedMapOf<String, RestPropertyDescriptionJS>()

    val collections = linkedMapOf<String, RestCollectionDescriptionJS>()

}


class RestMetaRegistryJS{
    val enums = linkedMapOf<String, RestEnumDescriptionJS>()

    val entities = linkedMapOf<String, RestEntityDescriptionJS>()

    val operations = linkedMapOf<String, RestOperationDescriptionJS>()

    companion object {
        fun get(): RestMetaRegistryJS {
            return EnvironmentJS.getPublished(RestMetaRegistryJS::class)
        }
    }
}




