/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.BaseIndexJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS
import com.gridnine.jasmine.web.core.model.rest.RestEntityDescriptionJS
import com.gridnine.jasmine.web.core.model.rest.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.model.rest.RestPropertyTypeJS
import com.gridnine.jasmine.web.core.model.ui.*


object RestSerializationUtilsJS {


    internal val restProviderFactory = object : ProviderFactoryJS {

        private fun createEntityDescription(descr: RestEntityDescriptionJS): ObjectMetadataProviderJS<BaseRestEntityJS> {
            return object : ObjectMetadataProviderJS<BaseRestEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    var extendsId = descr.extends
                    while (extendsId != null) {
                        val parentDescr = RestMetaRegistryJS.get().entities[extendsId]
                                ?: throw IllegalStateException("no entity found for id $extendsId")
                        fillProperties(parentDescr)
                        fillCollections(parentDescr)
                        extendsId = parentDescr.extends
                    }
                    fillProperties(descr)
                    fillCollections(descr)

                }

                private fun fillProperties(descr: RestEntityDescriptionJS){
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
                    }
                }
                private fun fillCollections(descr: RestEntityDescriptionJS){
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


        private fun createNavigationTableColumnDataDescription(): ObjectMetadataProviderJS<NavigationTableColumnDataJS> {
            return object : ObjectMetadataProviderJS<NavigationTableColumnDataJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(NavigationTableColumnDataJS.reference, SerializablePropertyTypeJS.ENTITY, EntityReferenceJS.qualifiedClassName, false))
                    properties.add(SerializablePropertyDescriptionJS(NavigationTableColumnDataJS.navigationKey, SerializablePropertyTypeJS.STRING, null, false))
                }

                override fun getPropertyValue(obj: NavigationTableColumnDataJS, id: String): Any? {
                    if(id == NavigationTableColumnDataJS.reference){
                        return obj.reference
                    }
                    if(id == NavigationTableColumnDataJS.navigationKey){
                        return obj.navigationKey
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }

                override fun getCollection(obj: NavigationTableColumnDataJS, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("class has no collections")
                }

                override fun setPropertyValue(obj: NavigationTableColumnDataJS, id: String, value: Any?) {
                    if(id == NavigationTableColumnDataJS.navigationKey){
                        obj.navigationKey = value as String?
                        return;
                    }
                    if(id == NavigationTableColumnDataJS.reference){
                        obj.reference = value as EntityReferenceJS?
                        return;
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }


            }
        }

        private fun createTileDescription(): ObjectMetadataProviderJS<TileDataJS<Any,Any>> {
            return object : ObjectMetadataProviderJS<TileDataJS<Any,Any>>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(TileDataJS.compactData, SerializablePropertyTypeJS.ENTITY, BaseEntityJS.qualifiedClassName, true))
                    properties.add(SerializablePropertyDescriptionJS(TileDataJS.fullData, SerializablePropertyTypeJS.ENTITY, BaseEntityJS.qualifiedClassName, true))
                }

                override fun getPropertyValue(obj: TileDataJS<Any,Any>, id: String): Any? {
                    if(id == TileDataJS.compactData){
                        return obj.compactData
                    }
                    if(id == TileDataJS.fullData){
                        return obj.fullData
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }

                override fun getCollection(obj: TileDataJS<Any,Any>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("class has no collections")
                }

                override fun setPropertyValue(obj: TileDataJS<Any,Any>, id: String, value: Any?) {
                    if(id == TileDataJS.compactData){
                        obj.compactData = value as Any
                        return;
                    }
                    if(id == TileDataJS.fullData){
                        obj.fullData = value as Any
                        return;
                    }
                    throw IllegalArgumentException("property $id does not exist")
                }

                override fun createInstance(): TileDataJS<Any, Any>? {
                    return TileDataJS()
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
            if(elementClassName == BaseEntityJS.qualifiedClassName){
                return true
            }
            if(elementClassName == BaseVMEntityJS.qualifiedClassName){
                return true
            }
            if(elementClassName == BaseVSEntityJS.qualifiedClassName){
                return true
            }
            if(elementClassName == BaseVVEntityJS.qualifiedClassName){
                return true
            }
            if(elementClassName == "com.gridnine.jasmine.server.core.model.ui.BaseVMEntity"){
                return true
            }
            val rett = RestMetaRegistryJS.get().entities[elementClassName]
            if(rett != null){
                return rett.abstract
            }
            return false

        }


        private fun toSerializableType(elementType: RestPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                RestPropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
                RestPropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
                RestPropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
                RestPropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
                RestPropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
                RestPropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
                RestPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
                RestPropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
                RestPropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
                RestPropertyTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
                RestPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            }
        }

        private fun toClassName(elementType: RestPropertyTypeJS, elementClassName: String?): String? {
            if (elementType == RestPropertyTypeJS.ENTITY_REFERENCE) {
                return EntityReferenceJS.qualifiedClassName
            }
            if (elementClassName != null) {
                return elementClassName
            }
            return null
        }


        override fun create(className: String): ObjectMetadataProviderJS<out Any> {
            if (EntityReferenceJS.qualifiedClassName == className) {
                return DomainSerializationUtilsJS.domainProviderFactory.create(className)
            }
            val restEntityDescription = RestMetaRegistryJS.get().entities[className]
            if(restEntityDescription != null){
                return createEntityDescription(restEntityDescription)
            }
            val indexDescr = DomainMetaRegistryJS.get().indexes[className]
            if (indexDescr != null) {
                return DomainSerializationUtilsJS.domainProviderFactory.create(className)
            }
            val assetDescr = DomainMetaRegistryJS.get().assets[className]
            if (assetDescr != null) {
                return DomainSerializationUtilsJS.domainProviderFactory.create(className)
            }
            if(UiMetaRegistryJS.get().viewModels.containsKey(className)){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }
            if(UiMetaRegistryJS.get().viewSettings.containsKey(className)){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }
            if(UiMetaRegistryJS.get().viewValidations.containsKey(className)){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }
            if(EnumSelectConfigurationJS.qualifiedClassName == className){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }
            if(EntitySelectConfigurationJS.qualifiedClassName == className){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }

            if(className.startsWith(TableConfigurationJS.serverQualifiedClassName) || className.startsWith(TableConfigurationJS.qualifiedClassName) ){
                return UiSerializationUtilsJS.uiProviderFactory.create(className)
            }
            if(className.startsWith(TileDataJS.serverQualifiedClassName) || className.startsWith(TileDataJS.qualifiedClassName) ){
                return createTileDescription()
            }
            if(className == NavigationTableColumnDataJS.qualifiedClassName || className == NavigationTableColumnDataJS.serverQualifiedClassName ){
                return createNavigationTableColumnDataDescription()
            }
            throw RuntimeException("unsupported type $className")

        }

    }


    fun <T : Any> serializeToString(obj: T): String {
        return CommonSerializationUtilsJS.serialize(obj,restProviderFactory)
    }



    fun <T : Any> deserializeFromJSON(qualifiedClasName:String, json: dynamic): T {
        return CommonSerializationUtilsJS.deserialize(qualifiedClasName, json, restProviderFactory)
    }

    fun <T : Any> deserialize(qualifiedClasName:String, content: String): T {
        return CommonSerializationUtilsJS.deserialize(qualifiedClasName, JSON.parse(content) as dynamic, restProviderFactory)
    }


}