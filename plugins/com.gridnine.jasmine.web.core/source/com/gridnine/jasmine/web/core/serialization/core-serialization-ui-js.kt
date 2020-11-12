/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.ui.*


internal class VMEntityMetadataProviderJS(description: VMEntityDescriptionJS) : ObjectMetadataProviderJS<BaseVMJS>() {

    init {
        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillCollections(desc: VMEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VMCollectionTypeJS, elementClassName: String?): String? {
        return when (elementType) {
            VMCollectionTypeJS.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VMCollectionTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            VMCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
        }
    }

    private fun fillProperties(desc: VMEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        return false
    }

    private fun toClassName(elementType: VMPropertyTypeJS, elementClassName: String?): String? {
        if (elementType == VMPropertyTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
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
            VMPropertyTypeJS.SELECT -> SerializablePropertyTypeJS.ENTITY
            VMPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVMJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVMJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVMJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }
}


internal class VSEntityMetadataProviderJS(description: VSEntityDescriptionJS) : ObjectMetadataProviderJS<BaseVSJS>() {

    init {
        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillCollections(desc: VSEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VSCollectionTypeJS, elementClassName: String?): String? {
        return when (elementType) {
            VSCollectionTypeJS.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VSCollectionTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            VSCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
        }
    }

    private fun fillProperties(desc: VSEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        return false
    }

    private fun toClassName(elementType: VSPropertyTypeJS, elementClassName: String?): String? {
        return when(elementType){
            VSPropertyTypeJS.TEXT_BOX_SETTINGS -> TextBoxConfigurationJS.qualifiedClassName
            VSPropertyTypeJS.PASSWORD_BOX_SETTINGS -> PasswordBoxConfigurationJS.qualifiedClassName
            VSPropertyTypeJS.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VSPropertyTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            VSPropertyTypeJS.TEXT_BOX_SETTINGS -> SerializablePropertyTypeJS.ENTITY
            VSPropertyTypeJS.PASSWORD_BOX_SETTINGS  -> SerializablePropertyTypeJS.ENTITY
            VSPropertyTypeJS.ENTITY  -> SerializablePropertyTypeJS.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVSJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVSJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVSJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }
}

internal class VVEntityMetadataProviderJS(description: VVEntityDescriptionJS) : ObjectMetadataProviderJS<BaseVVJS>() {

    init {
        fillProperties(description)
        fillCollections(description)
        isAbstract = false
    }

    private fun fillCollections(desc: VVEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun toClassName(elementType: VVCollectionTypeJS, elementClassName: String?): String? {
        return when (elementType) {
            VVCollectionTypeJS.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VVCollectionTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            VVCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
        }
    }

    private fun fillProperties(desc: VVEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        return false
    }

    private fun toClassName(elementType: VVPropertyTypeJS, elementClassName: String?): String? {
        return when(elementType){
            VVPropertyTypeJS.STRING -> null
            VVPropertyTypeJS.ENTITY -> elementClassName
        }
    }

    private fun toSerializableType(elementType: VVPropertyTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            VVPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            VVPropertyTypeJS.ENTITY  -> SerializablePropertyTypeJS.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseVVJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseVVJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseVVJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }
}