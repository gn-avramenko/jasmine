/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.CustomEntityDescription
import com.gridnine.jasmine.common.core.meta.CustomMetaRegistry
import com.gridnine.jasmine.common.core.meta.CustomType
import com.gridnine.jasmine.common.core.meta.MiscEntityDescription
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.core.model.ObjectReference

internal open class CustomEntityMetadataProvider(description: CustomEntityDescription) : ObjectMetadataProvider<BaseIntrospectableObject>() {

    private val hasUid:Boolean
    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = CustomMetaRegistry.get().entities[extendsId]
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
        hasUid= description.id != ObjectReference::class.qualifiedName &&  getAllProperties().find{ it.id == BaseIdentity.uid} != null
    }

    private fun fillMaps(desc: CustomEntityDescription) {
        desc.maps.values.forEach {
            addMap(SerializableMapDescription(it.id, toSerializableType(it.keyClassType), toClassName(it.keyClassType, it.keyClassName)
                , toSerializableType(it.valueClassType), toClassName(it.keyClassType, it.valueClassName), CommonSerializationUtils.isAbstractClass(it.valueClassName), CommonSerializationUtils.isAbstractClass(it.valueClassName)))
        }
    }

    private fun fillCollections(desc: CustomEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtils.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: CustomEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtils.isAbstractClass(it.className)))
        }
    }

    private fun toClassName(elementType: CustomType, elementClassName: String?): String? {
        if (elementType == CustomType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: CustomType): SerializablePropertyType {
        return when (elementType) {
            CustomType.LONG -> SerializablePropertyType.LONG
            CustomType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            CustomType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            CustomType.INT -> SerializablePropertyType.INT
            CustomType.ENUM -> SerializablePropertyType.ENUM
            CustomType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            CustomType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            CustomType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            CustomType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
            CustomType.STRING -> SerializablePropertyType.STRING
            CustomType.ENTITY -> SerializablePropertyType.ENTITY
            CustomType.CLASS -> SerializablePropertyType.CLASS
        }
    }

    override fun getPropertyValue(obj: BaseIntrospectableObject, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIntrospectableObject, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIntrospectableObject, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return hasUid
    }

    override fun getMap(obj: BaseIntrospectableObject, id: String): MutableMap<Any?, Any?> {
        return obj.getMap(id)
    }
}
