/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.common.core.meta.CustomMetaRegistryJS
import com.gridnine.jasmine.common.core.meta.CustomTypeJS
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.meta.RestMetaRegistryJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS


internal abstract class ObjectMetadataProviderJS<T:Any> {
    internal val propertiesMap = linkedMapOf<String, SerializablePropertyDescriptionJS>()
    internal var collectionsMap = linkedMapOf<String, SerializableCollectionDescriptionJS>()
    internal val properties = arrayListOf<SerializablePropertyDescriptionJS>()
    internal var collections = arrayListOf<SerializableCollectionDescriptionJS>()
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

        val restEntity = RestMetaRegistryJS.get().entities[className]
        if(restEntity != null){
            return restEntity.isAbstract
        }
        val customEntity = CustomMetaRegistryJS.get().entities[className]
        if(customEntity != null){
            return customEntity.isAbstract
        }
        return false
    }

    fun toSerializableType(type: CustomTypeJS): SerializablePropertyTypeJS {
        return when (type){
            CustomTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            CustomTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            CustomTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            CustomTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            CustomTypeJS.CLASS -> SerializablePropertyTypeJS.CLASS
            CustomTypeJS.INT -> SerializablePropertyTypeJS.INT
            CustomTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            CustomTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            CustomTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            CustomTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            CustomTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            CustomTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
        }
    }

    fun toClassName(elementType: CustomTypeJS, elementClassName: String?): String? {
        if (elementType == CustomTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }
}