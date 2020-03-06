/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.*
import com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS

internal object DomainSerializationUtilsJS {


    val domainProviderFactory = object : ProviderFactoryJS {

        private fun createEntityReferenceDescription(): ObjectMetadataProviderJS<BaseRestEntityJS> {
            return object : ObjectMetadataProviderJS<BaseRestEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(BaseEntityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityReferenceJS.type, SerializablePropertyTypeJS.STRING, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityReferenceJS.caption, SerializablePropertyTypeJS.STRING, null, false))
                }
                override fun getCollection(obj: BaseRestEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseRestEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseRestEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createIndexDescription(descr: IndexDescriptionJS): ObjectMetadataProviderJS<BaseRestEntityJS> {
            return object : ObjectMetadataProviderJS<BaseRestEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(BaseIndexJS.document, SerializablePropertyTypeJS.ENTITY, EntityReferenceJS.qualifiedClassName, false))
                    properties.add(SerializablePropertyDescriptionJS(BaseIndexJS.navigationKey, SerializablePropertyTypeJS.STRING, null, false))
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }
                override fun getCollection(obj: BaseRestEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseRestEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseRestEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createAssetDescription(descr: AssetDescriptionJS): ObjectMetadataProviderJS<BaseRestEntityJS> {
            return object : ObjectMetadataProviderJS<BaseRestEntityJS>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(BaseEntityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }
                override fun getCollection(obj: BaseRestEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseRestEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseRestEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }



        private fun isAbstractClass(elementClassName: String?): Boolean {
            if(elementClassName == null){
                return false
            }
            if(elementClassName == BaseIndexJS.qualifiedClassName){
                return true
            }
            return false

        }

        private fun toSerializableType(elementType: DatabaseCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                DatabaseCollectionTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
                DatabaseCollectionTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
                DatabaseCollectionTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            }
        }
        
        private fun toSerializableType(elementType: DatabasePropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                DatabasePropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
                DatabasePropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
                DatabasePropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
                DatabasePropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
                DatabasePropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
                DatabasePropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
                DatabasePropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
                DatabasePropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
                DatabasePropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
                DatabasePropertyTypeJS.TEXT -> SerializablePropertyTypeJS.STRING
            }
        }

        private fun toClassName(elementType: DatabasePropertyTypeJS, elementClassName: String?): String? {
           return  when(elementType){
                DatabasePropertyTypeJS.ENTITY_REFERENCE ->EntityReferenceJS.qualifiedClassName
                else -> elementClassName
            }
        }

        private fun toClassName(elementType: DatabaseCollectionTypeJS, elementClassName: String?): String? {
            return  when(elementType){
                DatabaseCollectionTypeJS.ENTITY_REFERENCE ->EntityReferenceJS.qualifiedClassName
                else -> elementClassName
            }
        }


        override fun create(className: String): ObjectMetadataProviderJS<out Any> {
            if(className == EntityReferenceJS.qualifiedClassName){
                return createEntityReferenceDescription()
            }
            val indexDescr = DomainMetaRegistryJS.get().indexes[className]
            if(indexDescr != null){
                return createIndexDescription(indexDescr)
            }
            val assetDescr = DomainMetaRegistryJS.get().assets[className]
            if(assetDescr != null){
                return createAssetDescription(assetDescr)
            }
            throw RuntimeException("unsupported type $className")
        }
    }


}