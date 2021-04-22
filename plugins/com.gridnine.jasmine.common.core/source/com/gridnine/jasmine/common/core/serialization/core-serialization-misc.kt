/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.MiscEntityDescription
import com.gridnine.jasmine.common.core.meta.MiscFieldType
import com.gridnine.jasmine.common.core.meta.MiscMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.core.model.ObjectReference


internal open class MiscEntityMetadataProvider(description: MiscEntityDescription) : ObjectMetadataProvider<BaseIntrospectableObject>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = MiscMetaRegistry.get().entities[extendsId]
                    ?: throw IllegalStateException("no rest entity found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
    }

    private fun fillCollections(desc: MiscEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtils.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: MiscEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtils.isAbstractClass(it.className)))
        }
    }



    private fun toClassName(elementType: MiscFieldType, elementClassName: String?): String? {
        if (elementType == MiscFieldType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: MiscFieldType): SerializablePropertyType {
        return when (elementType) {
            MiscFieldType.LONG -> SerializablePropertyType.LONG
            MiscFieldType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            MiscFieldType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            MiscFieldType.INT -> SerializablePropertyType.INT
            MiscFieldType.ENUM -> SerializablePropertyType.ENUM
            MiscFieldType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            MiscFieldType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            MiscFieldType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            MiscFieldType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
            MiscFieldType.STRING -> SerializablePropertyType.STRING
            MiscFieldType.ENTITY -> SerializablePropertyType.ENTITY
            MiscFieldType.CLASS -> SerializablePropertyType.CLASS
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
        return false
    }
}
