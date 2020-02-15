/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.serialization

import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.model.ui.*


@Suppress("UNCHECKED_CAST")
object UiSerializationUtils {


    internal val uiProviderFactory = object : ProviderFactory {
        override fun create(className: String): ObjectMetadataProvider<out Any> {
            val vmDescription = UiMetaRegistry.get().viewModels[className]
            if(vmDescription != null){
                return object : ObjectMetadataProvider<BaseVMEntity>() {
                    override fun hasUid(): Boolean {
                        return true
                    }

                    init {
                        properties.add(SerializablePropertyDescription("uid", SerializablePropertyType.STRING, null, false))
                        fillProperties(vmDescription)
                        fillCollections(vmDescription)
                    }

                    private fun fillCollections(vmDescription: VMEntityDescription) {
                        vmDescription.collections.values.forEach {
                            collections.add(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                        }

                    }

                    private fun fillProperties(desc: VMEntityDescription) {
                        desc.properties.values.forEach {
                            properties.add(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
                        }
                    }

                    override fun getCollection(obj: BaseVMEntity, id: String): MutableCollection<Any> {
                        return obj.getCollection(id)
                    }

                    override fun setPropertyValue(obj: BaseVMEntity, id: String, value: Any?) {
                        obj.setValue(id, value)
                    }

                    override fun getPropertyValue(obj: BaseVMEntity, id: String): Any? {
                        return obj.getValue(id)
                    }

                }
            }
            val vsDescription = UiMetaRegistry.get().viewSettings[className]
            if(vsDescription != null){

                return object : ObjectMetadataProvider<BaseVSEntity>() {
                    init{
                        fillProperties(vsDescription)
                        fillCollections(vsDescription)
                    }

                    override fun hasUid(): Boolean {
                        return false
                    }
                    private fun fillCollections(vsDescription: VSEntityDescription) {
                        vsDescription.collections.values.forEach {
                            collections.add(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                        }

                    }

                    private fun fillProperties(vsDescription: VSEntityDescription) {
                        vsDescription.properties.values.forEach {
                            properties.add(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
                        }
                    }

                    override fun getCollection(obj: BaseVSEntity, id: String): MutableCollection<Any> {
                        return obj.getCollection(id)
                    }

                    override fun setPropertyValue(obj: BaseVSEntity, id: String, value: Any?) {
                        obj.setValue(id, value)
                    }

                    override fun getPropertyValue(obj: BaseVSEntity, id: String): Any? {
                        return obj.getValue(id)
                    }

                }
            }
            val vvDescription = UiMetaRegistry.get().viewValidations[className]
            if(vvDescription != null){

                return object : ObjectMetadataProvider<BaseVVEntity>() {
                    init{
                        fillProperties(vvDescription)
                        fillCollections(vvDescription)
                    }

                    override fun hasUid(): Boolean {
                        return false
                    }
                    private fun fillCollections(vvDescription: VVEntityDescription) {
                        vvDescription.collections.values.forEach {
                            collections.add(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                        }

                    }

                    private fun fillProperties(vvDescription: VVEntityDescription) {
                        vvDescription.properties.values.forEach {
                            properties.add(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
                        }
                    }

                    override fun getCollection(obj: BaseVVEntity, id: String): MutableCollection<Any> {
                        return obj.getCollection(id)
                    }

                    override fun setPropertyValue(obj: BaseVVEntity, id: String, value: Any?) {
                        obj.setValue(id, value)
                    }

                    override fun getPropertyValue(obj: BaseVVEntity, id: String): Any? {
                        return obj.getValue(id)
                    }

                }
            }
            if(EnumSelectConfiguration::class.qualifiedName == className){
                return object : ObjectMetadataProvider<EnumSelectConfiguration<*>>() {


                    override fun hasUid(): Boolean {
                        return false
                    }

                    override fun getCollection(obj: EnumSelectConfiguration<*>, id: String): MutableCollection<Any> {
                        throw IllegalArgumentException("no collection fields")
                    }

                    override fun setPropertyValue(obj: EnumSelectConfiguration<*>, id: String, value: Any?) {
                        throw IllegalArgumentException("no field with id $id")
                    }

                    override fun getPropertyValue(obj: EnumSelectConfiguration<*>, id: String): Any? {
                        throw IllegalArgumentException("no field with id $id")
                    }

                }
            }
            if(EntityAutocompleteConfiguration::class.qualifiedName == className){
                return object : ObjectMetadataProvider<EntityAutocompleteConfiguration>() {
                    init{
                        properties.add(SerializablePropertyDescription("limit", SerializablePropertyType.INT, null, false))
                        collections.add(SerializableCollectionDescription("dataSources", SerializablePropertyType.STRING, null, false))
                    }

                    override fun hasUid(): Boolean {
                        return false
                    }

                    override fun getCollection(obj: EntityAutocompleteConfiguration, id: String): MutableCollection<Any> {
                        if("dataSources" == id){
                            return obj.dataSources as MutableCollection<Any>
                        }
                        throw IllegalArgumentException("no collection fields")
                    }

                    override fun setPropertyValue(obj: EntityAutocompleteConfiguration, id: String, value: Any?) {
                        if("limit" == id){
                            obj.limit = value as Int
                            return
                        }
                        throw IllegalArgumentException("no field with id $id")
                    }

                    override fun getPropertyValue(obj: EntityAutocompleteConfiguration, id: String): Any? {
                        if("limit" == id){
                            return obj.limit
                        }
                        throw IllegalArgumentException("no field with id $id")
                    }

                }
            }

            if(TableConfiguration::class.qualifiedName == className){
                return object:ObjectMetadataProvider<TableConfiguration<BaseVSEntity>>(){
                    init{
                        properties.add(SerializablePropertyDescription("columnSettings", SerializablePropertyType.ENTITY, BaseVSEntity::class.qualifiedName, true))
                    }
                    override fun getPropertyValue(obj: TableConfiguration<BaseVSEntity>, id: String): Any? {
                        if(id == "columnSettings"){
                            return obj.columnSettings
                        }
                        throw IllegalArgumentException("no field with id $id")
                    }

                    override fun getCollection(obj: TableConfiguration<BaseVSEntity>, id: String): MutableCollection<Any> {
                        throw IllegalArgumentException("no collection with id $id")
                    }

                    override fun setPropertyValue(obj: TableConfiguration<BaseVSEntity>, id: String, value: Any?) {
                        if(id == "columnSettings"){
                            obj.columnSettings = value as BaseVSEntity
                            return
                        }
                        throw IllegalArgumentException("no field with id $id")
                    }

                    override fun hasUid(): Boolean {
                        return false
                    }

                }
            }
            throw IllegalArgumentException("unsupported class name $className")
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
                if(BaseVMEntity::class.qualifiedName == elementClassName){
                    return true
                }
                if(BaseVSEntity::class.qualifiedName == elementClassName){
                    return true
                }
                if(BaseVVEntity::class.qualifiedName == elementClassName){
                    return true
                }
            }
        }
        return false

    }

    private fun toClassName(elementType: VVPropertyType,className:String?): String? {
        return when (elementType) {
            VVPropertyType.STRING -> null
            VVPropertyType.ENTITY -> className
        }
    }

    private fun toClassName(elementType: VVCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VVCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toClassName(elementType: VMPropertyType, elementClassName: String?): String? {
        return when (elementType) {
            VMPropertyType.LONG -> null
            VMPropertyType.LOCAL_DATE_TIME -> null
            VMPropertyType.LOCAL_DATE -> null
            VMPropertyType.INT -> null
            VMPropertyType.ENUM -> elementClassName
            VMPropertyType.ENTITY_REFERENCE ->EntityReference::class.qualifiedName
            VMPropertyType.ENTITY -> elementClassName
            VMPropertyType.BOOLEAN -> null
            VMPropertyType.BIG_DECIMAL -> null
            VMPropertyType.STRING -> null
            VMPropertyType.SELECT -> SelectItem::class.qualifiedName
        }
    }

    private fun toClassName(elementType: VMCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VMCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toClassName(elementType: VSCollectionType, elementClassName: String?): String? {
        return when(elementType){
            VSCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toClassName(elementType: VSPropertyType, elementClassName: String?): String? {
        return when(elementType){
            VSPropertyType.ENUM_SELECT -> EnumSelectConfiguration::class.qualifiedName
            VSPropertyType.ENTITY -> elementClassName
            VSPropertyType.ENTITY_AUTOCOMPLETE ->"${EntityAutocompleteConfiguration::class.qualifiedName}<$elementClassName>"
            VSPropertyType.SELECT ->SelectConfiguration::class.qualifiedName
        }
    }


    private fun toSerializableType(elementType: VSPropertyType): SerializablePropertyType {
        return when (elementType) {
            VSPropertyType.ENUM_SELECT -> SerializablePropertyType.ENTITY
            VSPropertyType.ENTITY -> SerializablePropertyType.ENTITY
            VSPropertyType.ENTITY_AUTOCOMPLETE -> SerializablePropertyType.ENTITY
            VSPropertyType.SELECT -> SerializablePropertyType.ENTITY
        }
    }


    private fun toSerializableType(elementType: VSCollectionType): SerializablePropertyType {
        return when (elementType) {
            VSCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    private fun toSerializableType(elementType: VVPropertyType): SerializablePropertyType {
        return when (elementType) {
            VVPropertyType.STRING -> SerializablePropertyType.STRING
            VVPropertyType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    private fun toSerializableType(elementType: VVCollectionType): SerializablePropertyType {
        return when (elementType) {
            VVCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }


    private fun toSerializableType(elementType: VMPropertyType): SerializablePropertyType {
        return when (elementType) {
            VMPropertyType.LONG -> SerializablePropertyType.LONG
            VMPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            VMPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            VMPropertyType.INT -> SerializablePropertyType.INT
            VMPropertyType.ENUM -> SerializablePropertyType.ENUM
            VMPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            VMPropertyType.ENTITY -> SerializablePropertyType.ENTITY
            VMPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            VMPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            VMPropertyType.STRING -> SerializablePropertyType.STRING
            VMPropertyType.SELECT -> SerializablePropertyType.ENTITY
        }
    }

    private fun toSerializableType(elementType: VMCollectionType): SerializablePropertyType {
        return when (elementType) {
            VMCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

}