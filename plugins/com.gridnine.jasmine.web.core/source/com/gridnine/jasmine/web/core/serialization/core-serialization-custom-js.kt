/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.common.core.meta.CustomEntityDescriptionJS
import com.gridnine.jasmine.common.core.meta.CustomMetaRegistryJS
import com.gridnine.jasmine.common.core.meta.CustomTypeJS
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS


internal open class CustomEntityMetadataProviderJS(description: CustomEntityDescriptionJS) : ObjectMetadataProviderJS<BaseIntrospectableObjectJS>() {

    private val hasUid:Boolean
    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = CustomMetaRegistryJS.get().entities[extendsId]
                    ?: throw IllegalStateException("no rest entity found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            fillMaps(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        fillMaps(description)
        isAbstract = description.isAbstract
        hasUid= description.id != ObjectReferenceJS.qualifiedClassName && getAllProperties().find{ it.id == BaseIdentityJS.uid} != null
    }

    private fun fillMaps(desc: CustomEntityDescriptionJS) {
        desc.maps.values.forEach {
            addMap(SerializableMapDescriptionJS(it.id, toSerializableType(it.keyClassType), toClassName(it.keyClassType, it.keyClassName)
                , toSerializableType(it.valueClassType), toClassName(it.keyClassType, it.valueClassName), CommonSerializationUtilsJS.isAbstractClass(it.valueClassName), CommonSerializationUtilsJS.isAbstractClass(it.valueClassName)))
        }
    }

    private fun fillCollections(desc: CustomEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtilsJS.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: CustomEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtilsJS.isAbstractClass(it.className)))
        }
    }

    private fun toClassName(elementType: CustomTypeJS, elementClassName: String?): String? {
        if (elementType == CustomTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: CustomTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            CustomTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            CustomTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            CustomTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            CustomTypeJS.INT -> SerializablePropertyTypeJS.INT
            CustomTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            CustomTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            CustomTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            CustomTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            CustomTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
            CustomTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            CustomTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            CustomTypeJS.CLASS -> SerializablePropertyTypeJS.CLASS
        }
    }

    override fun getPropertyValue(obj: BaseIntrospectableObjectJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIntrospectableObjectJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIntrospectableObjectJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return hasUid
    }

    override fun getMap(obj: BaseIntrospectableObjectJS, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}
