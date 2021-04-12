/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.RestEntityDescription
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestPropertyType
import com.gridnine.jasmine.common.core.model.BaseRestEntity
import com.gridnine.jasmine.common.core.model.ObjectReference


internal open class RestEntityMetadataProvider(description: RestEntityDescription) : ObjectMetadataProvider<BaseRestEntity>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = RestMetaRegistry.get().entities[extendsId]
                    ?: throw IllegalStateException("no rest entity found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
    }

    private fun fillCollections(desc: RestEntityDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtils.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: RestEntityDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtils.isAbstractClass(it.className)))
        }
    }



    private fun toClassName(elementType: RestPropertyType, elementClassName: String?): String? {
        if (elementType == RestPropertyType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: RestPropertyType): SerializablePropertyType {
        return when (elementType) {
            RestPropertyType.LONG -> SerializablePropertyType.LONG
            RestPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            RestPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            RestPropertyType.INT -> SerializablePropertyType.INT
            RestPropertyType.ENUM -> SerializablePropertyType.ENUM
            RestPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            RestPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            RestPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            RestPropertyType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
            RestPropertyType.STRING -> SerializablePropertyType.STRING
            RestPropertyType.ENTITY -> SerializablePropertyType.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseRestEntity, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseRestEntity, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseRestEntity, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }
}
