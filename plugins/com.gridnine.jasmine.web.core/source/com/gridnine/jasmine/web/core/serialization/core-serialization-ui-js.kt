/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*

internal object UiSerializationUtilsJS {


    val uiProviderFactory = object : ProviderFactoryJS {
        private fun createTableConfigurationDescription():ObjectMetadataProviderJS<TableConfigurationJS<BaseVSEntityJS>>{
            return object:ObjectMetadataProviderJS<TableConfigurationJS<BaseVSEntityJS>>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(TableConfigurationJS.columnSettings, SerializablePropertyTypeJS.ENTITY,BaseVSEntityJS.qualifiedClassName , true))
                }
                override fun getPropertyValue(obj: TableConfigurationJS<BaseVSEntityJS>, id: String): Any? {
                    if(id == TableConfigurationJS.columnSettings){
                        return obj.columnSettings
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: TableConfigurationJS<BaseVSEntityJS>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: TableConfigurationJS<BaseVSEntityJS>, id: String, value: Any?) {
                    if(id == TableConfigurationJS.columnSettings){
                        obj.columnSettings = value as BaseVSEntityJS
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

                override fun createInstance(): TableConfigurationJS<BaseVSEntityJS>? {
                    return TableConfigurationJS()
                }

            }
        }

        private fun createEnumSelectConfigurationDescription(): ObjectMetadataProviderJS<EnumSelectConfigurationJS<*>> {
            return object : ObjectMetadataProviderJS<EnumSelectConfigurationJS<*>>() {
                override fun hasUid(): Boolean {
                    return false
                }


                override fun getCollection(obj: EnumSelectConfigurationJS<*>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("unsupported property $id")
                }

                override fun setPropertyValue(obj: EnumSelectConfigurationJS<*>, id: String, value: Any?) {
                    throw IllegalArgumentException("unsupported property $id")
                }

                override fun getPropertyValue(obj: EnumSelectConfigurationJS<*>, id: String): Any? {
                    throw IllegalArgumentException("unsupported property $id")
                }

            }
        }



        private fun createEntityAutocompleteConfigurationDescription(): ObjectMetadataProviderJS<EntitySelectConfigurationJS> {
            return object : ObjectMetadataProviderJS<EntitySelectConfigurationJS>() {
                init {
                    properties.add(SerializablePropertyDescriptionJS(EntitySelectConfigurationJS.limit, SerializablePropertyTypeJS.INT, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntitySelectConfigurationJS.nullAllowed, SerializablePropertyTypeJS.BOOLEAN, null, false))
                    collections.add(SerializableCollectionDescriptionJS(EntitySelectConfigurationJS.dataSources, SerializablePropertyTypeJS.STRING, null, false))
                }

                override fun hasUid(): Boolean {
                    return false
                }

                override fun getCollection(obj: EntitySelectConfigurationJS, id: String): MutableCollection<Any> {
                    if (EntitySelectConfigurationJS.dataSources == id) {
                        return obj.dataSources as MutableCollection<Any>
                    }
                    throw IllegalArgumentException("no collection fields")
                }

                override fun setPropertyValue(obj: EntitySelectConfigurationJS, id: String, value: Any?) {
                    if (EntitySelectConfigurationJS.nullAllowed == id) {
                        obj.nullAllowed = value as Boolean
                        return
                    }
                    if (EntitySelectConfigurationJS.limit == id) {
                        obj.limit = value as Int
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getPropertyValue(obj: EntitySelectConfigurationJS, id: String): Any? {
                    if (EntitySelectConfigurationJS.nullAllowed == id) {
                        return obj.nullAllowed
                    }
                    if (EntitySelectConfigurationJS.limit == id) {
                        return obj.limit
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

            }
        }


        private fun createVMEntityDescription(descr: VMEntityDescriptionJS): ObjectMetadataProviderJS<BaseVMEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVMEntityJS>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(BaseEntityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), it.elementClassName, isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVMEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVMEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVMEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createVSEntityDescription(descr: VSEntityDescriptionJS): ObjectMetadataProviderJS<BaseVSEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVSEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVSEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVSEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVSEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createVVEntityDescription(descr: VVEntityDescriptionJS): ObjectMetadataProviderJS<BaseVVEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVVEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVVEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVVEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVVEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun isAbstractClass(elementClassName: String?): Boolean {
            if (elementClassName == null) {
                return false
            }
            if (elementClassName == "com.gridnine.jasmine.web.core.model.ui.BaseVMEntity") {
                return true
            }
            if (elementClassName == "com.gridnine.jasmine.web.core.model.ui.BaseVSEntity") {
                return true
            }
            if (elementClassName == "com.gridnine.jasmine.web.core.model.ui.BaseVVEntity") {
                return true
            }
            if (elementClassName == "com.gridnine.jasmine.server.core.model.ui.BaseVMEntity") {
                return true
            }
            return false

        }

        private fun toSerializableType(elementType: VMCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VMCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }
        

        private fun toSerializableType(elementType: VMPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VMPropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
                VMPropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
                VMPropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
                VMPropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
                VMPropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
                VMPropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
                VMPropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
                VMPropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
                VMPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
                VMPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
                VMPropertyTypeJS.SELECT -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VMPropertyTypeJS, elementClassName: String?): String? {
            return when (elementType){
                VMPropertyTypeJS.ENTITY_REFERENCE ->EntityReferenceJS.qualifiedClassName
                else -> elementClassName
            }
        }

        private fun toSerializableType(elementType: VSCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VSCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }
        
        private fun toSerializableType(elementType: VSPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VSPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.SELECT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.ENUM_SELECT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.ENTITY_AUTOCOMPLETE -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VSPropertyTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VSPropertyTypeJS.ENTITY -> elementClassName
                VSPropertyTypeJS.ENUM_SELECT -> EnumSelectConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.SELECT -> SelectConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.ENTITY_AUTOCOMPLETE -> EntitySelectConfigurationJS.qualifiedClassName

            }
        }

        private fun toClassName(elementType: VSCollectionTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VSCollectionTypeJS.ENTITY -> elementClassName
            }
        }

        private fun toSerializableType(elementType: VVPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VVPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
                VVPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VVPropertyTypeJS, className:String?): String? {
            return when (elementType) {
                VVPropertyTypeJS.STRING -> null
                VVPropertyTypeJS.ENTITY ->{
                    className
                }
            }
        }

        private fun toSerializableType(elementType: VVCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VVCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VVCollectionTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VVCollectionTypeJS.ENTITY -> elementClassName
            }
        }

        override fun create(className: String): ObjectMetadataProviderJS<out Any> {
            if (className == EnumSelectConfigurationJS.qualifiedClassName) {
                return createEnumSelectConfigurationDescription()
            }
            if (className == EntitySelectConfigurationJS.qualifiedClassName) {
                return createEntityAutocompleteConfigurationDescription()
            }

            if(className.startsWith(TableConfigurationJS.serverQualifiedClassName) || className.startsWith(TableConfigurationJS.qualifiedClassName) ){
                return createTableConfigurationDescription()
            }
            val vmDescription = UiMetaRegistryJS.get().viewModels[className]
            if (vmDescription != null) {
                return createVMEntityDescription(vmDescription)
            }
            val vsDescription = UiMetaRegistryJS.get().viewSettings[className]
            if (vsDescription != null) {
                return createVSEntityDescription(vsDescription)
            }
            val vvDescription = UiMetaRegistryJS.get().viewValidations[className]
            if (vvDescription != null) {
                return createVVEntityDescription(vvDescription)
            }
            throw RuntimeException("unsupported type $className")
        }
    }

}