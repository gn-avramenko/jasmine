/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper


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


class RestMapDescription(id: String, val keyClassType: RestPropertyType, val valueClassType: RestPropertyType): BaseModelElementDescription(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}

class RestEntityDescription(id:String) : BaseRestElementDescription(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = LinkedHashMap<String, RestPropertyDescription>()

    val collections = LinkedHashMap<String, RestCollectionDescription>()

    val maps = LinkedHashMap<String, RestMapDescription>()

}


class RestMetaRegistry: Disposable {
    val enums = linkedMapOf<String, RestEnumDescription>()

    val entities = linkedMapOf<String, RestEntityDescription>()

    val rests = linkedMapOf<String, RestDescription>()

    val groups = linkedMapOf<String, RestGroupDescription>()

    val operations = linkedMapOf<String, RestOperationDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(RestMetaRegistry::class)
        fun get() = wrapper.get()
    }
}


