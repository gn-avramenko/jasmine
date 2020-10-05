/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.serialization


import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.custom.CustomMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry


abstract class ObjectMetadataProvider<T : Any> {
    private val propertiesMap = linkedMapOf<String, SerializablePropertyDescription>()
    private var collectionsMap = linkedMapOf<String, SerializableCollectionDescription>()
    private val properties = arrayListOf<SerializablePropertyDescription>()
    private var collections = arrayListOf<SerializableCollectionDescription>()
    var isAbstract:Boolean = false
    fun getProperty(id: String) = propertiesMap[id]
    fun getCollection(id: String) = collectionsMap[id]
    fun getAllProperties() = properties
    fun getAllCollections() = collections
    fun addProperty(prop: SerializablePropertyDescription) {
        propertiesMap[prop.id] = prop
        properties.add(prop)
    }

    fun addCollection(coll: SerializableCollectionDescription) {
        collectionsMap[coll.id] = coll
        collections.add(coll)
    }

    abstract fun getPropertyValue(obj: T, id: String): Any?
    abstract fun getCollection(obj: T, id: String): MutableCollection<Any>
    abstract fun setPropertyValue(obj: T, id: String, value: Any?)
    abstract fun hasUid(): Boolean
}


enum class SerializablePropertyType {
    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    BIG_DECIMAL,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    CLASS,
    BYTE_ARRAY
}

class SerializablePropertyDescription(val id: String, val type: SerializablePropertyType, val className: String?, val isAbstract: Boolean)


class SerializableCollectionDescription(val id: String, val elementType: SerializablePropertyType, val elementClassName: String?, val isAbstract: Boolean)

internal object CommonSerializationUtils{
    fun isAbstractClass(className:String?):Boolean{
        if(className == null){
            return false
        }
        val domainDocument = DomainMetaRegistry.get().documents[className]
        if(domainDocument != null){
            return domainDocument.isAbstract
        }
        val nestedDocument = DomainMetaRegistry.get().nestedDocuments[className]
        if(nestedDocument != null){
            return nestedDocument.isAbstract
        }

        val restEntity = RestMetaRegistry.get().entities[className]
        if(restEntity != null){
            return restEntity.isAbstract
        }
        val customEntity = CustomMetaRegistry.get().entities[className]
        if(customEntity != null){
            return customEntity.isAbstract
        }
        return false
    }
}
