/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.common.core.meta.MiscEntityDescriptionJS
import com.gridnine.jasmine.common.core.meta.MiscFieldTypeJS
import com.gridnine.jasmine.common.core.meta.MiscMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS

internal open class MiscEntityMetadataProviderJS(description: MiscEntityDescriptionJS) : ObjectMetadataProviderJS<BaseIntrospectableObjectJS>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = MiscMetaRegistryJS.get().entities[extendsId]
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
    }

    private fun fillMaps(desc: MiscEntityDescriptionJS) {
        desc.maps.values.forEach {
            addMap(SerializableMapDescriptionJS(it.id, toSerializableType(it.keyClassType), toClassName(it.keyClassType, it.keyClassName)
                , toSerializableType(it.valueClassType), toClassName(it.keyClassType, it.valueClassName), CommonSerializationUtilsJS.isAbstractClass(it.valueClassName), CommonSerializationUtilsJS.isAbstractClass(it.valueClassName)))
        }
    }

    private fun fillCollections(desc: MiscEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtilsJS.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: MiscEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtilsJS.isAbstractClass(it.className)))
        }
    }



    private fun toClassName(elementType: MiscFieldTypeJS, elementClassName: String?): String? {
        if (elementType == MiscFieldTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: MiscFieldTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            MiscFieldTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            MiscFieldTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            MiscFieldTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            MiscFieldTypeJS.INT -> SerializablePropertyTypeJS.INT
            MiscFieldTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            MiscFieldTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            MiscFieldTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            MiscFieldTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            MiscFieldTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
            MiscFieldTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            MiscFieldTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            MiscFieldTypeJS.CLASS -> SerializablePropertyTypeJS.STRING
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
        return false
    }

    override fun getMap(obj: BaseIntrospectableObjectJS, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}
