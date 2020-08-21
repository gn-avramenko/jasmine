/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.serialization

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.custom.CustomEntityDescription
import com.gridnine.jasmine.server.core.model.custom.CustomMetaRegistry
import com.gridnine.jasmine.server.core.model.custom.CustomType
import com.gridnine.jasmine.server.core.model.domain.ObjectReference

internal open class CustomEntityMetadataProvider(description: CustomEntityDescription) : ObjectMetadataProvider<BaseIntrospectableObject>() {

    private val hasUid:Boolean;
    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = CustomMetaRegistry.get().entities[extendsId]
                    ?: throw IllegalStateException("no rest entity found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
        hasUid= getAllProperties().find{ it.id == BaseIdentity.uid} != null
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
}
