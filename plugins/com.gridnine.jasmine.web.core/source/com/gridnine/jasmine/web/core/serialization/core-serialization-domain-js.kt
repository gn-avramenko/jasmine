/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.server.core.model.common.BaseIdentityJS
import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.server.core.model.domain.*


internal open class DomainDocumentMetadataProvider(description: DocumentDescriptionJS) : ObjectMetadataProviderJS<BaseIdentityJS>() {

    init {
        addProperty(SerializablePropertyDescriptionJS(BaseIdentityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
        if(description.root) {
            addProperty(SerializablePropertyDescriptionJS(BaseDocumentJS.revision, SerializablePropertyTypeJS.INT, null, false))
        }
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = DomainMetaRegistryJS.get().documents[extendsId]?: throw XeptionJS.forDeveloper("no document found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
    }

    private fun fillCollections(desc: DocumentDescriptionJS) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtilsJS.isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: DocumentDescriptionJS) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtilsJS.isAbstractClass(it.className)))
        }
    }


    private fun toClassName(elementType: DocumentPropertyTypeJS, elementClassName: String?): String? {
        if (elementType == DocumentPropertyTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: DocumentPropertyTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            DocumentPropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            DocumentPropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            DocumentPropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            DocumentPropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
            DocumentPropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            DocumentPropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            DocumentPropertyTypeJS.NESTED_DOCUMENT -> SerializablePropertyTypeJS.ENTITY
            DocumentPropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            DocumentPropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            DocumentPropertyTypeJS.BYTE_ARRAY -> SerializablePropertyTypeJS.BYTE_ARRAY
            DocumentPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
        }
    }

    override fun getPropertyValue(obj: BaseIdentityJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIdentityJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIdentityJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return true
    }
}


internal open class BaseDomainIndexMetadataProviderJS(description: BaseIndexDescriptionJS) : ObjectMetadataProviderJS<BaseIdentityJS>() {

    init {
        addProperty(SerializablePropertyDescriptionJS(BaseIdentityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
        description.properties.values.forEach {
            addProperty(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), CommonSerializationUtilsJS.isAbstractClass(it.className)))
        }
        description.collections.values.forEach {
            addCollection(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), CommonSerializationUtilsJS.isAbstractClass(it.elementClassName)))
        }
    }





    private fun toSerializableType(elementType: DatabasePropertyTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            DatabasePropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
            DatabasePropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
            DatabasePropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
            DatabasePropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            DatabasePropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            DatabasePropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
            DatabasePropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
            DatabasePropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            DatabasePropertyTypeJS.TEXT -> SerializablePropertyTypeJS.STRING
        }
    }

    private fun toSerializableType(elementType: DatabaseCollectionTypeJS): SerializablePropertyTypeJS {
        return when (elementType) {
            DatabaseCollectionTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
            DatabaseCollectionTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
            DatabaseCollectionTypeJS.STRING -> SerializablePropertyTypeJS.STRING
        }
    }

    private fun toClassName(elementType: DatabasePropertyTypeJS, elementClassName: String?): String? {
        if (elementType == DatabasePropertyTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    private fun toClassName(elementType: DatabaseCollectionTypeJS, elementClassName: String?): String? {
        if (elementType == DatabaseCollectionTypeJS.ENTITY_REFERENCE) {
            return ObjectReferenceJS.qualifiedClassName
        }
        return elementClassName
    }

    override fun getPropertyValue(obj: BaseIdentityJS, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIdentityJS, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIdentityJS, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return true
    }
}

internal class DomainIndexMetadataProvider(description: IndexDescriptionJS) : BaseDomainIndexMetadataProviderJS(description) {
    init{
        addProperty(SerializablePropertyDescriptionJS(BaseIdentityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
        addProperty(SerializablePropertyDescriptionJS(BaseIndexJS.document, SerializablePropertyTypeJS.ENTITY, ObjectReferenceJS.qualifiedClassName, false))
    }
}

internal class DomainAssetMetadataProvider(description: AssetDescriptionJS) : BaseDomainIndexMetadataProviderJS(description) {
    init{
        addProperty(SerializablePropertyDescriptionJS(BaseIdentityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
    }
}