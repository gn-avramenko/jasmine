/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.serialization

import com.google.gson.JsonObject
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.model.rest.BaseRestEntity
import com.gridnine.jasmine.server.core.model.rest.RestEntityDescription
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestPropertyType
import com.gridnine.jasmine.server.core.model.ui.*
import kotlin.reflect.KClass

object RestSerializationUtils {


    private val restProviderFactory = object : ProviderFactory {

        private fun createEntityDescription(descr: RestEntityDescription): ObjectMetadataProvider<BaseRestEntity> {
            return object : ObjectMetadataProvider<BaseRestEntity>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    var extendsId = descr.extends
                    while (extendsId != null) {
                        val parentDescr = RestMetaRegistry.get().entities[extendsId]
                                ?: throw IllegalStateException("no entity found for id $extendsId")
                        fillProperties(parentDescr)
                        fillCollections(parentDescr)
                        extendsId = parentDescr.extends
                    }
                    fillProperties(descr)
                    fillCollections(descr)

                }

                private fun fillProperties(descr: RestEntityDescription){
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.type, it.className)))
                    }
                }
                private fun fillCollections(descr: RestEntityDescription){
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementType, it.elementClassName)))
                    }
                }
                override fun getCollection(obj: BaseRestEntity, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseRestEntity, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseRestEntity, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }


        private fun createTileDescription(): ObjectMetadataProvider<TileData<Any,Any>> {
            return object : ObjectMetadataProvider<TileData<Any,Any>>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescription(TileData.compactData, SerializablePropertyType.ENTITY, BaseEntity::class.qualifiedName, true))
                    properties.add(SerializablePropertyDescription(TileData.fullData, SerializablePropertyType.ENTITY, BaseEntity::class.qualifiedName, true))
                }

                override fun getPropertyValue(obj: TileData<Any,Any>, id: String): Any? {
                    if(id == TileData.compactData){
                        return obj.compactData
                    }
                    if(id == TileData.fullData){
                        return obj.fullData
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }

                override fun getCollection(obj: TileData<Any,Any>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("class has no collections")
                }

                override fun setPropertyValue(obj: TileData<Any,Any>, id: String, value: Any?) {
                    if(id == TileData.compactData){
                        obj.compactData = value as Any
                        return;
                    }
                    if(id == TileData.fullData){
                        obj.fullData = value as Any
                        return;
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }


            }
        }

        private fun createNavigationTableColumnDataDescription(): ObjectMetadataProvider<NavigationTableColumnData> {
            return object : ObjectMetadataProvider<NavigationTableColumnData>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescription(NavigationTableColumnData.reference, SerializablePropertyType.ENTITY, EntityReference::class.qualifiedName, false))
                    properties.add(SerializablePropertyDescription(NavigationTableColumnData.navigationKey, SerializablePropertyType.STRING, null, false))
                }

                override fun getPropertyValue(obj: NavigationTableColumnData, id: String): Any? {
                    if(id == NavigationTableColumnData.reference){
                        return obj.reference
                    }
                    if(id == NavigationTableColumnData.navigationKey){
                        return obj.navigationKey
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }

                override fun getCollection(obj: NavigationTableColumnData, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("class has no collections")
                }

                override fun setPropertyValue(obj: NavigationTableColumnData, id: String, value: Any?) {
                    if(id == NavigationTableColumnData.navigationKey){
                        obj.navigationKey = value as String?
                        return;
                    }
                    if(id == NavigationTableColumnData.reference){
                        obj.reference = value as EntityReference<*>?
                        return;
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }


            }
        }

        private fun isAbstractClass(elementType:RestPropertyType, elementClassName: String?): Boolean {
            if(elementType == RestPropertyType.ENTITY_REFERENCE){
                return false
            }
            if(elementClassName == null){
                return false
            }
            if(elementClassName == BaseIndex::class.qualifiedName){
                return true
            }
            if(elementClassName == BaseEntity::class.qualifiedName){
                return true
            }
            if(elementClassName == BaseVMEntity::class.qualifiedName){
                return true
            }
            if(elementClassName == BaseVSEntity::class.qualifiedName){
                return true
            }
            if(elementClassName == BaseVVEntity::class.qualifiedName){
                return true
            }
            val rett = RestMetaRegistry.get().entities[elementClassName]
            if(rett != null){
                return rett.abstract
            }
            return false

        }


        private fun toSerializableType(elementType: RestPropertyType): SerializablePropertyType {
            return when (elementType) {
                RestPropertyType.LONG -> SerializablePropertyType.LONG
                RestPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
                RestPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
                RestPropertyType.INT -> SerializablePropertyType.INT
                RestPropertyType.ENUM -> SerializablePropertyType.ENUM
                RestPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
                RestPropertyType.ENTITY -> SerializablePropertyType.ENTITY
                RestPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
                RestPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
                RestPropertyType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
                RestPropertyType.STRING -> SerializablePropertyType.STRING
            }
        }

        private fun toClassName(elementType: RestPropertyType, elementClassName: String?): String? {
            if (elementType == RestPropertyType.ENTITY_REFERENCE) {
                return EntityReference::class.qualifiedName
            }
            if (elementClassName != null) {
                return elementClassName
            }
            return null
        }


        override fun create(className: String): ObjectMetadataProvider<out Any> {
            if (EntityReference::class.qualifiedName == className) {
                return DomainSerializationUtils.domainProviderFactory.create(className)
            }
            val restEntityDescription = RestMetaRegistry.get().entities[className]
            if(restEntityDescription != null){
                return createEntityDescription(restEntityDescription)
            }
            val docDescr = DomainMetaRegistry.get().documents[className]
            if (docDescr != null) {
                return DomainSerializationUtils.domainProviderFactory.create(className)
            }
            val nestedDocDescr = DomainMetaRegistry.get().nestedDocuments[className]
            if (nestedDocDescr != null) {
                return DomainSerializationUtils.domainProviderFactory.create(className)
            }
            val indexDescr = DomainMetaRegistry.get().indexes[className]
            if (indexDescr != null) {
                return DomainSerializationUtils.domainProviderFactory.create(className)
            }
            val assetDescr = DomainMetaRegistry.get().assets[className]
            if (assetDescr != null) {
                return DomainSerializationUtils.domainProviderFactory.create(className)
            }
            if(UiMetaRegistry.get().viewModels.containsKey(className)){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(UiMetaRegistry.get().viewSettings.containsKey(className)){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(UiMetaRegistry.get().viewValidations.containsKey(className)){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(EnumSelectConfiguration::class.qualifiedName == className){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(EntityAutocompleteConfiguration::class.qualifiedName == className){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(TableConfiguration::class.qualifiedName == className){
                return UiSerializationUtils.uiProviderFactory.create(className)
            }
            if(className.startsWith(TileData::class.qualifiedName!!)){
                return createTileDescription()
            }
            if(className == NavigationTableColumnData::class.qualifiedName){
                return createNavigationTableColumnDataDescription()
            }
            throw RuntimeException("unsupported type $className")

        }

    }

    fun <T : Any> serializeToJson(obj: T): JsonObject {
        return SerializationUtils.serialize(obj, restProviderFactory, false)
    }

    fun <T : Any> serializeToString(obj: T): String {
        return SerializationUtils.serializeToString(obj,restProviderFactory, false)
    }

    fun <T : Any> serializeToByteArray(obj: T): ByteArray {
        return SerializationUtils.serializeToByteArray(obj, restProviderFactory, false)
    }

    fun <T : Any> deserialize(cls: KClass<T>, content: String): T {
        return SerializationUtils.deserialize(cls, content, restProviderFactory)
    }

    fun <T : Any> deserialize(cls: KClass<T>, content: ByteArray): T {
        return SerializationUtils.deserialize(cls, content, restProviderFactory)
    }
}