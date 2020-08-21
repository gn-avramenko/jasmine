/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistryJS


internal abstract class ObjectMetadataProviderJS<T:Any> {
    private val propertiesMap = linkedMapOf<String, SerializablePropertyDescriptionJS>()
    private var collectionsMap = linkedMapOf<String, SerializableCollectionDescriptionJS>()
    private val properties = arrayListOf<SerializablePropertyDescriptionJS>()
    private var collections = arrayListOf<SerializableCollectionDescriptionJS>()
    var isAbstract:Boolean = false
    fun getProperty(id: String) = propertiesMap[id]
    fun getCollection(id: String) = collectionsMap[id]
    fun getAllProperties() = properties
    fun getAllCollections() = collections
    fun addProperty(prop: SerializablePropertyDescriptionJS) {
        propertiesMap[prop.id] = prop
        properties.add(prop)
    }

    fun addCollection(coll: SerializableCollectionDescriptionJS) {
        collectionsMap[coll.id] = coll
        collections.add(coll)
    }

    abstract fun getPropertyValue(obj: T, id: String): Any?
    abstract fun getCollection(obj: T, id: String): MutableCollection<Any>
    abstract fun setPropertyValue(obj: T, id: String, value: Any?)
    abstract fun hasUid(): Boolean
    fun createInstance(): T?{
        return null
    }
}

internal enum class SerializablePropertyTypeJS {
    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    CLASS,
    BIG_DECIMAL,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY
}

internal class SerializablePropertyDescriptionJS(val id: String, val type: SerializablePropertyTypeJS, val className: String?, val isAbstract:Boolean)

internal class SerializableCollectionDescriptionJS(val id: String, val elementType: SerializablePropertyTypeJS, val elementClassName: String?, val isAbstract: Boolean)

internal object CommonSerializationUtilsJS{
    fun isAbstractClass(className:String?):Boolean{
        if(className == null){
            return false
        }
        val domainDocument = DomainMetaRegistryJS.get().documents[className]
        if(domainDocument != null){
            return domainDocument.isAbstract
        }

        val restEntity = RestMetaRegistryJS.get().entities[className]
        if(restEntity != null){
            return restEntity.isAbstract
        }
        return false
    }
}