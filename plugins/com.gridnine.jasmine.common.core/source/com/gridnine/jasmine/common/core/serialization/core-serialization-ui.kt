/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*


internal class VMEntityMetadataProvider(description: VMEntityDescription) : ObjectMetadataProvider<BaseVM>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = UiMetaRegistry.get().viewModels[extendsId]
            extendsId = if(parentDescr != null){
                fillProperties(parentDescr)
                fillCollections(parentDescr)
                parentDescr.extendsId
            } else {
                val customDescr = CustomMetaRegistry.get().entities[extendsId]!!
                fillProperties(customDescr)
                fillCollections(customDescr)
                customDescr.extendsId
            }
        }

        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillCollections(desc: VMEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VMCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VMCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VMCollectionType): SerializablePropertyType {
        return when (elementType) {
            VMCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    private fun fillProperties(desc: CustomEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, CommonSerializationUtils.toSerializableType(it.type), CommonSerializationUtils.toClassName(it.type, it.className), false))
        }
    }

    private fun fillCollections(desc: CustomEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, CommonSerializationUtils.toSerializableType(it.elementType), CommonSerializationUtils.toClassName(it.elementType, it.elementClassName), false))
        }
    }



    private fun fillProperties(desc: VMEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        if(elementClassName != null){
            val ett = CustomMetaRegistry.get().entities[elementClassName]
            if(ett != null){
                return ett.isAbstract
            }
        }
        return false

    }

    private fun toClassName(elementType: VMPropertyType, elementClassName: String?): String? {
        if (elementType == VMPropertyType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: VMPropertyType): SerializablePropertyType {
        return when (elementType) {
            VMPropertyType.LONG -> SerializablePropertyType.LONG
            VMPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            VMPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            VMPropertyType.INT -> SerializablePropertyType.INT
            VMPropertyType.ENUM -> SerializablePropertyType.ENUM
            VMPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            VMPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            VMPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            VMPropertyType.STRING -> SerializablePropertyType.STRING
            VMPropertyType.SELECT -> SerializablePropertyType.ENTITY
            VMPropertyType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVM, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVM, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVM, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }

    override fun getMap(obj: BaseVM, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}


internal class VSEntityMetadataProvider(description: VSEntityDescription) : ObjectMetadataProvider<BaseVS>() {


    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = UiMetaRegistry.get().viewSettings[extendsId]
            extendsId = if(parentDescr != null){
                fillProperties(parentDescr)
                fillCollections(parentDescr)
                parentDescr.extendsId
            } else {
                val customDescr = CustomMetaRegistry.get().entities[extendsId]!!
                fillProperties(customDescr)
                fillCollections(customDescr)
                customDescr.extendsId
            }
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillProperties(desc: CustomEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, CommonSerializationUtils.toSerializableType(it.type), CommonSerializationUtils.toClassName(it.type, it.className), false))
        }
    }

    private fun fillCollections(desc: CustomEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, CommonSerializationUtils.toSerializableType(it.elementType), CommonSerializationUtils.toClassName(it.elementType, it.elementClassName), false))
        }
    }

    private fun fillCollections(desc: VSEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VSCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VSCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VSCollectionType): SerializablePropertyType {
        return when (elementType) {
            VSCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    private fun fillProperties(desc: VSEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        if(elementClassName != null){
            val ett = CustomMetaRegistry.get().entities[elementClassName]
            if(ett != null){
                return ett.isAbstract
            }
        }
        return false
    }

    private fun toClassName(elementType: VSPropertyType, elementClassName: String?): String? {
        return when(elementType){
            VSPropertyType.TEXT_BOX_SETTINGS -> TextBoxConfiguration::class.qualifiedName
            VSPropertyType.PASSWORD_BOX_SETTINGS -> PasswordBoxConfiguration::class.qualifiedName
            VSPropertyType.ENTITY -> elementClassName
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> BigDecimalBoxConfiguration::class.qualifiedName
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS -> IntegerNumberBoxConfiguration::class.qualifiedName
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> BooleanBoxConfiguration::class.qualifiedName
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS -> EntitySelectBoxConfiguration::class.qualifiedName
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> EnumSelectBoxConfiguration::class.qualifiedName
            VSPropertyType.DATE_BOX_SETTINGS -> DateBoxConfiguration::class.qualifiedName
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> DateTimeBoxConfiguration::class.qualifiedName
            VSPropertyType.STRING -> null
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS ->  GeneralSelectBoxConfiguration::class.qualifiedName
            VSPropertyType.RICH_TEXT_EDITOR_SETTINGS -> RichTextEditorConfiguration::class.qualifiedName
        }
    }

    private fun toSerializableType(elementType: VSPropertyType): SerializablePropertyType {
        return when (elementType) {
            VSPropertyType.TEXT_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.PASSWORD_BOX_SETTINGS  -> SerializablePropertyType.ENTITY
            VSPropertyType.ENTITY -> SerializablePropertyType.ENTITY
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.DATE_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.STRING -> SerializablePropertyType.STRING
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS -> SerializablePropertyType.ENTITY
            VSPropertyType.RICH_TEXT_EDITOR_SETTINGS -> SerializablePropertyType.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }

    override fun getMap(obj: BaseVS, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}

internal class VVEntityMetadataProvider(description: VVEntityDescription) : ObjectMetadataProvider<BaseVV>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = UiMetaRegistry.get().viewValidations[extendsId]
            extendsId = if(parentDescr != null){
                fillProperties(parentDescr)
                fillCollections(parentDescr)
                parentDescr.extendsId
            } else {
                val customDescr = CustomMetaRegistry.get().entities[extendsId]!!
                fillProperties(customDescr)
                fillCollections(customDescr)
                customDescr.extendsId
            }
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillProperties(desc: CustomEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, CommonSerializationUtils.toSerializableType(it.type), CommonSerializationUtils.toClassName(it.type, it.className), false))
        }
    }

    private fun fillCollections(desc: CustomEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, CommonSerializationUtils.toSerializableType(it.elementType), CommonSerializationUtils.toClassName(it.elementType, it.elementClassName), false))
        }
    }

    private fun fillCollections(desc: VVEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VVCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VVCollectionType.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VVCollectionType): SerializablePropertyType {
        return when (elementType) {
            VVCollectionType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    private fun fillProperties(desc: VVEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        if(elementClassName != null){
            val ett = CustomMetaRegistry.get().entities[elementClassName]
            if(ett != null){
                return ett.isAbstract
            }
        }
        return false

    }

    private fun toClassName(elementType: VVPropertyType, elementClassName: String?): String? {
        return when(elementType){
            VVPropertyType.STRING -> null
            VVPropertyType.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VVPropertyType): SerializablePropertyType {
        return when (elementType) {
            VVPropertyType.STRING -> SerializablePropertyType.STRING
            VVPropertyType.ENTITY  -> SerializablePropertyType.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVV, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVV, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVV, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }

    override fun getMap(obj: BaseVV, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}