/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.serialization

import com.google.gson.JsonObject
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.*
import kotlin.reflect.KClass


object DomainSerializationUtils {

    internal val domainProviderFactory = object : ProviderFactory {

        private fun createDocumentDescription(descr: BaseDocumentDescription): ObjectMetadataProvider<BaseEntity> {
            return object : ObjectMetadataProvider<BaseEntity>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescription(BaseEntity.uid, SerializablePropertyType.STRING, null, false))
                    var extendsId = descr.extendsId
                    while (extendsId != null){
                        val parentDescr = DomainMetaRegistry.get().documents[extendsId]?:DomainMetaRegistry.get().nestedDocuments[extendsId]?:
                        throw IllegalStateException("no document found for id $extendsId")
                        fillProperties(parentDescr)
                        fillCollections(parentDescr)
                        extendsId = parentDescr.extendsId
                    }
                    fillProperties(descr)
                    fillCollections(descr)
                }

                private fun fillCollections(desc: BaseDocumentDescription) {
                    desc.collections.values.forEach {
                        collections.add(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }

                private fun fillProperties(desc: BaseDocumentDescription) {
                    desc.properties.values.forEach {
                        properties.add(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
                    }
                }

                override fun getCollection(obj: BaseEntity, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseEntity, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseEntity, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createEntityReferenceDescription(): ObjectMetadataProvider<EntityReference<BaseEntity>>{
            return object: ObjectMetadataProvider<EntityReference<BaseEntity>>(){
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescription(BaseEntity.uid, SerializablePropertyType.STRING, null, false))
                    properties.add(SerializablePropertyDescription(EntityReference.type, SerializablePropertyType.CLASS, null, false))
                    properties.add(SerializablePropertyDescription(EntityReference.caption, SerializablePropertyType.STRING, null, false))
                }

                override fun getPropertyValue(obj: EntityReference<BaseEntity>, id: String): Any? {
                    return when (id) {
                        BaseEntity.uid -> obj.uid
                        EntityReference.type -> obj.type
                        EntityReference.caption -> obj.caption
                        else -> throw IllegalArgumentException("no property $id")
                    }
                }

                override fun getCollection(obj: EntityReference<BaseEntity>, id: String): MutableCollection<Any> {
                    throw  RuntimeException("no collection")
                }

                override fun setPropertyValue(obj: EntityReference<BaseEntity>, id: String, value: Any?) {
                    @Suppress("UNCHECKED_CAST")
                    when (id) {
                        BaseEntity.uid-> obj.uid = value as String
                        EntityReference.type -> obj.type = value as KClass<BaseEntity>
                        EntityReference.caption -> obj.caption = value as String
                    }
                }
            }
        }

        private fun createIndexDescription(indexDescr:IndexDescription): ObjectMetadataProvider<BaseIndex<BaseDocument>>{
            return object : ObjectMetadataProvider<BaseIndex<BaseDocument>>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescription(BaseEntity.uid, SerializablePropertyType.STRING, null, false))
                    properties.add(SerializablePropertyDescription(BaseIndex.navigationKey, SerializablePropertyType.STRING, null, false))
                    properties.add(SerializablePropertyDescription(BaseIndex.document, SerializablePropertyType.ENTITY, EntityReference::class.qualifiedName, false))
                    fillProperties(properties, indexDescr)
                    fillCollections(collections, indexDescr)
                }

                override fun getPropertyValue(obj: BaseIndex<BaseDocument>, id: String): Any? {
                    return obj.getValue(id)
                }

                override fun getCollection(obj: BaseIndex<BaseDocument>, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseIndex<BaseDocument>, id: String, value: Any?) {
                    obj.setValue(id, value)
                }
            }

        }

        private fun createAssetDescription(assetDescr:AssetDescription) :ObjectMetadataProvider<BaseAsset>{
            return object : ObjectMetadataProvider<BaseAsset>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescription(BaseEntity.uid, SerializablePropertyType.STRING, null, false))
                    properties.add(SerializablePropertyDescription(BaseAsset.createdBy, SerializablePropertyType.STRING, null, false))
                    properties.add(SerializablePropertyDescription(BaseAsset.modifiedBy, SerializablePropertyType.ENTITY, EntityReference::class.qualifiedName, false))
                    properties.add(SerializablePropertyDescription(BaseAsset.created, SerializablePropertyType.LOCAL_DATE_TIME, null, false))
                    properties.add(SerializablePropertyDescription(BaseAsset.modifiedBy, SerializablePropertyType.LOCAL_DATE_TIME, null, false))

                    fillProperties(properties, assetDescr)
                    fillCollections(collections, assetDescr)
                }

                override fun getPropertyValue(obj: BaseAsset, id: String): Any? {
                    return obj.getValue(id)
                }

                override fun getCollection(obj: BaseAsset, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseAsset, id: String, value: Any?) {
                    obj.setValue(id, value)
                }
            }
        }

        private fun isAbstractClass(elementClassName: String?): Boolean {
            if (elementClassName != null) {
                val desc = DomainMetaRegistry.get().documents[elementClassName]
                if (desc != null) {
                    return desc.isAbstract
                } else {
                    val nd = DomainMetaRegistry.get().nestedDocuments[elementClassName]
                    if (nd != null) {
                        return nd.isAbstract
                    }
                }
            }
            return false

        }

        private fun toClassName(elementType: DocumentPropertyType, elementClassName: String?): String? {
            if (elementType == DocumentPropertyType.ENTITY_REFERENCE) {
                return EntityReference::class.qualifiedName
            }
            return elementClassName
        }

        private fun toSerializableType(elementType: DocumentPropertyType): SerializablePropertyType {
            return when (elementType) {
                DocumentPropertyType.LONG -> SerializablePropertyType.LONG
                DocumentPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
                DocumentPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
                DocumentPropertyType.INT -> SerializablePropertyType.INT
                DocumentPropertyType.ENUM -> SerializablePropertyType.ENUM
                DocumentPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
                DocumentPropertyType.NESTED_DOCUMENT -> SerializablePropertyType.ENTITY
                DocumentPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
                DocumentPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
                DocumentPropertyType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
                DocumentPropertyType.STRING -> SerializablePropertyType.STRING
            }
        }

        override fun create(className: String): ObjectMetadataProvider<out Any> {
            if (EntityReference::class.qualifiedName == className) {
                return createEntityReferenceDescription()
            }
            val docDescr = DomainMetaRegistry.get().documents[className]
            if (docDescr != null) {
                return createDocumentDescription(docDescr)
            }
            val nestedDocDescr = DomainMetaRegistry.get().nestedDocuments[className]
            if (nestedDocDescr != null) {
                return createDocumentDescription(nestedDocDescr)
            }
            val indexDescr = DomainMetaRegistry.get().indexes[className]
            if (indexDescr != null) {
               return createIndexDescription(indexDescr)
            }

            val assetDescr = DomainMetaRegistry.get().assets[className]
            if (assetDescr != null) {
                return createAssetDescription(assetDescr)
            }
            throw RuntimeException("unsupported type $className")

        }

        private fun fillCollections(collections: ArrayList<SerializableCollectionDescription>, indexDescr: BaseIndexDescription) {
            indexDescr.collections.values.forEach { coll ->
                collections.add(SerializableCollectionDescription(coll.id, toSerializableType(coll.elementType), toClassName(coll.elementType, coll.elementClassName), isAbstractClass(coll.elementClassName)))
            }
        }


        private fun fillProperties(properties: ArrayList<SerializablePropertyDescription>, indexDescr: BaseIndexDescription) {
            indexDescr.properties.values.forEach { prop ->
                properties.add(SerializablePropertyDescription(prop.id, toSerializableType(prop.type), toClassName(prop.type, prop.className), isAbstractClass(prop.className)))
            }
        }

        private fun toSerializableType(elementType: DatabasePropertyType): SerializablePropertyType {
            return when (elementType) {
                DatabasePropertyType.LONG -> SerializablePropertyType.LONG
                DatabasePropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
                DatabasePropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
                DatabasePropertyType.INT -> SerializablePropertyType.INT
                DatabasePropertyType.ENUM -> SerializablePropertyType.ENUM
                DatabasePropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
                DatabasePropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
                DatabasePropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
                DatabasePropertyType.STRING -> SerializablePropertyType.STRING
                DatabasePropertyType.TEXT -> SerializablePropertyType.STRING
            }
        }

        private fun toSerializableType(elementType: DatabaseCollectionType): SerializablePropertyType {
            return when (elementType) {
                DatabaseCollectionType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
                DatabaseCollectionType.ENUM -> SerializablePropertyType.ENUM
                DatabaseCollectionType.STRING -> SerializablePropertyType.STRING
            }
        }

        private fun toClassName(elementType: DatabasePropertyType, elementClassName: String?): String? {
            if (elementType == DatabasePropertyType.ENTITY_REFERENCE) {
                return EntityReference::class.qualifiedName
            }
            return elementClassName
        }

        private fun toClassName(elementType: DatabaseCollectionType, elementClassName: String?): String? {
            if (elementType == DatabaseCollectionType.ENTITY_REFERENCE) {
                return EntityReference::class.qualifiedName
            }
            return elementClassName
        }

    }

    fun <T : Any> serializeToJson(obj: T): JsonObject {
        return SerializationUtils.serialize(obj, domainProviderFactory, false)
    }

    fun <T : Any> serializeToString(obj: T): String {
        return SerializationUtils.serializeToString(obj, domainProviderFactory, false)
    }

    fun <T : Any> serializeToByteArray(obj: T): ByteArray {
        return SerializationUtils.serializeToByteArray(obj, domainProviderFactory, false)
    }

    fun <T : Any> deserialize(cls: KClass<T>, content: String): T {
        return SerializationUtils.deserialize(cls, content, domainProviderFactory)
    }

    fun <T : Any> deserialize(cls: KClass<T>, content: ByteArray): T {
        return SerializationUtils.deserialize(cls, content, domainProviderFactory)
    }
}