/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.CustomMetaRegistry
import com.gridnine.jasmine.common.core.meta.CustomType
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.model.ObjectReference


abstract class ObjectMetadataProvider<T : Any> {
    private val propertiesMap = linkedMapOf<String, SerializablePropertyDescription>()
    private var collectionsMap = linkedMapOf<String, SerializableCollectionDescription>()
    private val properties = arrayListOf<SerializablePropertyDescription>()
    private var collections = arrayListOf<SerializableCollectionDescription>()
    private var maps = arrayListOf<SerializableMapDescription>()
    private val mapsMap = linkedMapOf<String, SerializableMapDescription>()
    var isAbstract:Boolean = false
    fun getProperty(id: String) = propertiesMap[id]
    fun getCollection(id: String) = collectionsMap[id]
    fun getAllProperties() = properties
    fun getAllCollections() = collections
    fun getAllMaps() = maps
    fun getMap(id:String) = mapsMap[id]
    fun addProperty(prop: SerializablePropertyDescription) {
        propertiesMap[prop.id] = prop
        properties.add(prop)
    }

    fun addCollection(coll: SerializableCollectionDescription) {
        collectionsMap[coll.id] = coll
        collections.add(coll)
    }

    fun addMap(map: SerializableMapDescription) {
        mapsMap[map.id] = map
        maps.add(map)
    }

    abstract fun getPropertyValue(obj: T, id: String): Any?
    abstract fun getCollection(obj: T, id: String): MutableCollection<Any>
    abstract fun setPropertyValue(obj: T, id: String, value: Any?)
    abstract fun getMap(obj:T, id:String): MutableMap<Any?,Any?>
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

class SerializableMapDescription(val id: String, val keyType: SerializablePropertyType, val keyClassName: String?, val valueType: SerializablePropertyType, val valueCassName: String?, val isKeyAbstract: Boolean, val isValueAbstract: Boolean)

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

    fun toSerializableType(type: CustomType): SerializablePropertyType {
        return when (type){
            CustomType.STRING -> SerializablePropertyType.STRING
            CustomType.ENUM -> SerializablePropertyType.ENUM
            CustomType.ENTITY -> SerializablePropertyType.ENTITY
            CustomType.LONG -> SerializablePropertyType.LONG
            CustomType.CLASS -> SerializablePropertyType.CLASS
            CustomType.INT -> SerializablePropertyType.INT
            CustomType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            CustomType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            CustomType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            CustomType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            CustomType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            CustomType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
        }
    }

    fun toClassName(elementType: CustomType, elementClassName: String?): String? {
        if (elementType == CustomType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }
}
