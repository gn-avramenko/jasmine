/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*

internal open class DomainDocumentMetadataProvider(description: DocumentDescription) : BaseDomainDocumentMetadataProvider(description) {
    init {
        addProperty(SerializablePropertyDescription(BaseDocument.revision, SerializablePropertyType.INT, null, false))
    }
}

internal open class NestedDocumentMetadataProvider(description: NestedDocumentDescription) : BaseDomainDocumentMetadataProvider(description)

internal open class BaseDomainDocumentMetadataProvider(description: BaseDocumentDescription) : ObjectMetadataProvider<BaseIdentity>() {

    init {
        addProperty(SerializablePropertyDescription(BaseIdentity.uid, SerializablePropertyType.STRING, null, false))
        var extendsId = description.extendsId
        while (extendsId != null) {
            val parentDescr = DomainMetaRegistry.get().documents[extendsId]
                    ?: DomainMetaRegistry.get().nestedDocuments[extendsId]
                    ?: throw IllegalStateException("no document found for id $extendsId")
            fillProperties(parentDescr)
            fillCollections(parentDescr)
            extendsId = parentDescr.extendsId
        }
        fillProperties(description)
        fillCollections(description)
        isAbstract = description.isAbstract
    }

    private fun fillCollections(desc: BaseDocumentDescription) {
        desc.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }

    private fun fillProperties(desc: BaseDocumentDescription) {
        desc.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
    }

    private fun isAbstractClass(elementClassName: String?): Boolean {
        if (elementClassName != null) {
            val desc = DomainMetaRegistry.get().documents[elementClassName]
            if (desc != null) {
                return desc.isAbstract
            } else {
                val nd = DomainMetaRegistry.get().nestedDocuments[elementClassName]
                if (nd != null) {
                    return nd.isAbstract
                }
            }
        }
        return false

    }

    private fun toClassName(elementType: DocumentPropertyType, elementClassName: String?): String? {
        if (elementType == DocumentPropertyType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toSerializableType(elementType: DocumentPropertyType): SerializablePropertyType {
        return when (elementType) {
            DocumentPropertyType.LONG -> SerializablePropertyType.LONG
            DocumentPropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            DocumentPropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            DocumentPropertyType.INT -> SerializablePropertyType.INT
            DocumentPropertyType.ENUM -> SerializablePropertyType.ENUM
            DocumentPropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            DocumentPropertyType.NESTED_DOCUMENT -> SerializablePropertyType.ENTITY
            DocumentPropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            DocumentPropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            DocumentPropertyType.BYTE_ARRAY -> SerializablePropertyType.BYTE_ARRAY
            DocumentPropertyType.STRING -> SerializablePropertyType.STRING
        }
    }

    override fun getPropertyValue(obj: BaseIdentity, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIdentity, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIdentity, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return true
    }
}


internal open class BaseDomainIndexMetadataProvider(description: BaseIndexDescription) : ObjectMetadataProvider<BaseIdentity>() {

    init {
        addProperty(SerializablePropertyDescription(BaseIdentity.uid, SerializablePropertyType.STRING, null, false))
        description.properties.values.forEach {
            addProperty(SerializablePropertyDescription(it.id, toSerializableType(it.type), toClassName(it.type, it.className), isAbstractClass(it.className)))
        }
        description.collections.values.forEach {
            addCollection(SerializableCollectionDescription(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
        }
    }


    private fun isAbstractClass(elementClassName: String?): Boolean {
        if (elementClassName != null) {
            val desc = DomainMetaRegistry.get().documents[elementClassName]
            if (desc != null) {
                return desc.isAbstract
            } else {
                val nd = DomainMetaRegistry.get().nestedDocuments[elementClassName]
                if (nd != null) {
                    return nd.isAbstract
                }
            }
        }
        return false

    }


    private fun toSerializableType(elementType: DatabasePropertyType): SerializablePropertyType {
        return when (elementType) {
            DatabasePropertyType.LONG -> SerializablePropertyType.LONG
            DatabasePropertyType.LOCAL_DATE_TIME -> SerializablePropertyType.LOCAL_DATE_TIME
            DatabasePropertyType.LOCAL_DATE -> SerializablePropertyType.LOCAL_DATE
            DatabasePropertyType.INT -> SerializablePropertyType.INT
            DatabasePropertyType.ENUM -> SerializablePropertyType.ENUM
            DatabasePropertyType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            DatabasePropertyType.BOOLEAN -> SerializablePropertyType.BOOLEAN
            DatabasePropertyType.BIG_DECIMAL -> SerializablePropertyType.BIG_DECIMAL
            DatabasePropertyType.STRING -> SerializablePropertyType.STRING
            DatabasePropertyType.TEXT -> SerializablePropertyType.STRING
        }
    }

    private fun toSerializableType(elementType: DatabaseCollectionType): SerializablePropertyType {
        return when (elementType) {
            DatabaseCollectionType.ENTITY_REFERENCE -> SerializablePropertyType.ENTITY
            DatabaseCollectionType.ENUM -> SerializablePropertyType.ENUM
            DatabaseCollectionType.STRING -> SerializablePropertyType.STRING
        }
    }

    private fun toClassName(elementType: DatabasePropertyType, elementClassName: String?): String? {
        if (elementType == DatabasePropertyType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    private fun toClassName(elementType: DatabaseCollectionType, elementClassName: String?): String? {
        if (elementType == DatabaseCollectionType.ENTITY_REFERENCE) {
            return ObjectReference::class.qualifiedName
        }
        return elementClassName
    }

    override fun getPropertyValue(obj: BaseIdentity, id: String): Any? {
        return obj.getValue(id)
    }

    override fun getCollection(obj: BaseIdentity, id: String): MutableCollection<Any> {
        return obj.getCollection(id)
    }

    override fun setPropertyValue(obj: BaseIdentity, id: String, value: Any?) {
        obj.setValue(id, value)
    }

    override fun hasUid(): Boolean {
        return true
    }
}

internal class DomainIndexMetadataProvider(description: IndexDescription) : BaseDomainIndexMetadataProvider(description) {
    init{
        addProperty(SerializablePropertyDescription(BaseIdentity.uid, SerializablePropertyType.STRING, null, false))
        addProperty(SerializablePropertyDescription(BaseIndex.documentField, SerializablePropertyType.ENTITY, ObjectReference::class.qualifiedName, false))
    }
}

internal class AssetMetadataProvider(description: AssetDescription) : BaseDomainIndexMetadataProvider(description) {
    init{
        addProperty(SerializablePropertyDescription(BaseIdentity.uid, SerializablePropertyType.STRING, null, false))
        addProperty(SerializablePropertyDescription(BaseAsset.revision, SerializablePropertyType.INT, null, false))
    }
}