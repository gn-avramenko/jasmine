/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused","UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.common.BaseModelElementDescription


enum class DocumentPropertyType {
    STRING,
    LOCAL_DATE,
    LOCAL_DATE_TIME,
    ENUM,
    BOOLEAN,
    BYTE_ARRAY,
    NESTED_DOCUMENT,
    ENTITY_REFERENCE,
    LONG,
    INT,
    BIG_DECIMAL
}

enum class DatabasePropertyType {
    STRING,
    TEXT,
    LOCAL_DATE,
    LOCAL_DATE_TIME,
    ENUM,
    BOOLEAN,
    ENTITY_REFERENCE,
    LONG,
    INT,
    BIG_DECIMAL
}



enum class DatabaseCollectionType {
    STRING,
    ENUM,
    ENTITY_REFERENCE
}


class DocumentPropertyDescription(id: String) : BaseModelElementDescription(id){
    var nonNullable = false
    var className: String? = null
    lateinit var type: DocumentPropertyType

    override fun getValue(propertyName: String): Any? {
        if(DocumentPropertyDescription.nonNullable == propertyName){
            return propertyName
        }
        if(DocumentPropertyDescription.className == propertyName){
            return className
        }
        if(DocumentPropertyDescription.type == propertyName){
            return type
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(DocumentPropertyDescription.nonNullable == propertyName){
            nonNullable = value as Boolean
            return
        }
        if(DocumentPropertyDescription.className == propertyName){
            className = value as String?
            return
        }
        if(DocumentPropertyDescription.type == propertyName){
            type = value as DocumentPropertyType
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val nonNullable = "nonNullable"
        const val className = "className"
        const val type = "type"
    }
}

class DocumentCollectionDescription(id: String) : BaseModelElementDescription(id){
    var elementClassName: String? = null
    lateinit var elementType: DocumentPropertyType
    var unique = false

    override fun getValue(propertyName: String): Any? {
        if(DocumentCollectionDescription.elementClassName == propertyName){
            return elementClassName
        }
        if(DocumentCollectionDescription.elementType == propertyName){
            return elementType
        }
        if(DocumentCollectionDescription.unique == propertyName){
            return unique
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(DocumentCollectionDescription.elementClassName == propertyName){
            elementClassName = value as String?
            return
        }
        if(DocumentCollectionDescription.elementType == propertyName){
            elementType = value as DocumentPropertyType
            return
        }
        if(DocumentCollectionDescription.unique == propertyName){
            unique = value as Boolean
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val elementClassName = "elementClassName"
        const val elementType = "elementType"
        const val unique = "unique"
    }
}


class DomainEnumItemDescription(id:String) : BaseModelElementDescription(id)


class DomainEnumDescription(id:String) : BaseModelElementDescription(id){
    val items = linkedMapOf<String, DomainEnumItemDescription>()
    override fun getMap(mapName: String): MutableMap<Any?, Any?> {
        if(DomainEnumDescription.items == mapName){
            return items as MutableMap<Any?, Any?>
        }
        return super.getMap(mapName)
    }
    companion object{
        const val items = "items"
    }
}

abstract class BaseDocumentDescription(id:String) : BaseModelElementDescription(id) {

    val properties = linkedMapOf<String, DocumentPropertyDescription>()

    val collections = linkedMapOf<String, DocumentCollectionDescription>()

    val codeInjections = arrayListOf<String>()

    var isAbstract=  false

    var extendsId: String? = null

    override fun getMap(mapName: String): MutableMap<Any?, Any?> {
        if(BaseDocumentDescription.properties == mapName){
            return properties as MutableMap<Any?, Any?>
        }
        if(BaseDocumentDescription.collections == mapName){
            return collections as MutableMap<Any?, Any?>
        }
        return super.getMap(mapName)
    }

    override fun getCollection(listName: String): MutableList<Any> {
        if(BaseDocumentDescription.codeInjections == listName){
            return codeInjections as MutableList<Any>
        }
        return super.getCollection(listName)
    }

    override fun getValue(propertyName: String): Any? {
        if(BaseDocumentDescription.isAbstract == propertyName){
            return isAbstract
        }
        if(BaseDocumentDescription.extendsId == propertyName){
            return extendsId
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(BaseDocumentDescription.isAbstract == propertyName){
            isAbstract = value as Boolean
            return
        }
        if(BaseDocumentDescription.extendsId == propertyName){
            extendsId = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val properties = "properties"
        const val collections = "collections"
        const val codeInjections = "codeInjections"
        const val isAbstract = "isAbstract"
        const val extendsId = "extendsId"
    }
}

class IndexPropertyDescription(id:String) : BaseModelElementDescription(id) {
    var nonNullable = false
    var className: String? = null
    lateinit var type: DatabasePropertyType

    override fun getValue(propertyName: String): Any? {
        if(IndexPropertyDescription.nonNullable == propertyName){
            return propertyName
        }
        if(IndexPropertyDescription.className == propertyName){
            return className
        }
        if(IndexPropertyDescription.type == propertyName){
            return type
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(IndexPropertyDescription.nonNullable == propertyName){
            nonNullable = value as Boolean
            return
        }
        if(IndexPropertyDescription.className == propertyName){
            className = value as String?
            return
        }
        if(IndexPropertyDescription.type == propertyName){
            type = value as DatabasePropertyType
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val nonNullable = "nonNullable"
        const val className = "className"
        const val type = "type"
    }
}

class IndexCollectionDescription(id:String) : BaseModelElementDescription(id) {

    var elementClassName: String? = null
    lateinit var elementType: DatabaseCollectionType
    var unique = false


    override fun getValue(propertyName: String): Any? {
        if(IndexCollectionDescription.elementClassName == propertyName){
            return elementClassName
        }
        if(IndexCollectionDescription.elementType == propertyName){
            return elementType
        }
        if(IndexCollectionDescription.unique == propertyName){
            return unique
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(IndexCollectionDescription.elementClassName == propertyName){
            elementClassName = value as String?
            return
        }
        if(IndexCollectionDescription.elementType == propertyName){
            elementType = value as DatabaseCollectionType
            return
        }
        if(IndexCollectionDescription.unique == propertyName){
            unique = value as Boolean
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val elementClassName = "elementClassName"
        const val elementType = "elementType"
        const val unique = "unique"
    }
}
abstract class BaseIndexDescription(id:String) : BaseModelElementDescription(id) {
    val properties = linkedMapOf<String, IndexPropertyDescription>()
    val collections = linkedMapOf<String, IndexCollectionDescription>()
    val codeInjections = arrayListOf<String>()

    override fun getMap(mapName: String): MutableMap<Any?, Any?> {
        if(BaseIndexDescription.properties == mapName){
            return properties as MutableMap<Any?, Any?>
        }
        if(BaseIndexDescription.collections == mapName){
            return collections as MutableMap<Any?, Any?>
        }
        return super.getMap(mapName)
    }

    override fun getCollection(listName: String): MutableList<Any> {
        if(BaseIndexDescription.codeInjections == listName){
            return codeInjections as MutableList<Any>
        }
        return super.getCollection(listName)
    }

    companion object{
        const val properties = "properties"
        const val collections = "collections"
        const val codeInjections = "codeInjections"
    }
}

class AssetDescription(id:String) : BaseIndexDescription(id)

class DocumentDescription(id:String) : BaseDocumentDescription(id)

class IndexDescription(id:String) : BaseIndexDescription(id) {

    lateinit var document: String

    override fun getValue(propertyName: String): Any? {
        if(IndexDescription.document == propertyName){
            return document
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(IndexDescription.document == propertyName){
            document = value as String
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val document = "document"
    }

}

class NestedDocumentDescription(id:String) : BaseDocumentDescription(id)

class DomainMetaRegistry:BaseIntrospectableObject(){
    val enums = linkedMapOf<String, DomainEnumDescription>()

    val documents = linkedMapOf<String, DocumentDescription>()

    val nestedDocuments = linkedMapOf<String, NestedDocumentDescription>()

    val indexes = linkedMapOf<String, IndexDescription>()

    val assets = linkedMapOf<String, AssetDescription>()

    override fun getMap(mapName: String): MutableMap<Any?, Any?> {
        if(DomainMetaRegistry.enums == mapName){
            return enums as MutableMap<Any?, Any?>
        }
        if(DomainMetaRegistry.documents == mapName){
            return documents as MutableMap<Any?, Any?>
        }
        if(DomainMetaRegistry.nestedDocuments == mapName){
            return nestedDocuments as MutableMap<Any?, Any?>
        }
        if(DomainMetaRegistry.indexes == mapName){
            return indexes as MutableMap<Any?, Any?>
        }
        if(DomainMetaRegistry.assets == mapName){
            return assets as MutableMap<Any?, Any?>
        }
        return super.getMap(mapName)
    }

    companion object {
        fun get(): DomainMetaRegistry {
            return Environment.getPublished(DomainMetaRegistry::class)
        }
        const val assets = "assets"
        const val documents = "documents"
        const val nestedDocuments = "nestedDocuments"
        const val indexes = "indexes"
        const val enums = "enums"
    }
}


