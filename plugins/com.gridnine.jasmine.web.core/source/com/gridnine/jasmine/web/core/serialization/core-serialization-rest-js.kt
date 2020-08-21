/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.rest.*

internal open class RestEntityMetadataProviderJS(description: RestEntityDescriptionJS) : ObjectMetadataProviderJS<BaseRestEntityJS>() {

    init {
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = RestMetaRegistryJS.get().entities[extendsId]
                    ?: throw IllegalStateException("no rest entity found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
    }

    private fun fillCollections(desc: RestEntityDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtilsJS.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: RestEntityDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtilsJS.isAbstractClass(it.className)))
        }
    }



    private fun toClassName(elementType: RestPropertyTypeJS, elementClassName: String?): String? {
        if (elementType == RestPropertyTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: RestPropertyTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            RestPropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            RestPropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            RestPropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            RestPropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
            RestPropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            RestPropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            RestPropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            RestPropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            RestPropertyTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
            RestPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            RestPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
        }
    }

    override fun getPropertyValue(obj: BaseRestEntityJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseRestEntityJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseRestEntityJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return false
    }
}
