/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseIdentityDescription
import com.gridnine.jasmine.server.core.model.common.BaseOwnedIdentityDescription
import java.util.*


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


class DocumentPropertyDescription(owner: String, id: String) : BaseOwnedIdentityDescription(owner, id){
    var notNullable = false
    var className: String? = null
    lateinit var type: DocumentPropertyType
}

class DocumentCollectionDescription(owner: String, id: String) : BaseOwnedIdentityDescription(owner, id){
    var elementClassName: String? = null
    lateinit var elementType: DocumentPropertyType
    var unique = false
}


class DomainEnumItemDescription(owner: String, id:String) : BaseOwnedIdentityDescription(owner, id)

class DomainEnumDescription(id:String) : BaseIdentityDescription(id){
    val items = linkedMapOf<String, DomainEnumItemDescription>()
}

abstract class BaseDocumentDescription(id:String) : BaseIdentityDescription(id) {

    val properties = LinkedHashMap<String, DocumentPropertyDescription>()

    val collections = LinkedHashMap<String, DocumentCollectionDescription>()

    val codeInjections = arrayListOf<String>()

    var isAbstract=  false

    var extendsId: String? = null
}

class IndexPropertyDescription(owner: String, id:String) : BaseOwnedIdentityDescription(owner, id) {
    var notNullable = false
    var className: String? = null
    lateinit var type: DatabasePropertyType
}

class IndexCollectionDescription(owner: String, id:String) : BaseOwnedIdentityDescription(owner, id) {

    var elementClassName: String? = null
    lateinit var elementType: DatabaseCollectionType
    var unique = false

}
abstract class BaseIndexDescription(id:String) : BaseIdentityDescription(id) {
    val properties = linkedMapOf<String, IndexPropertyDescription>()
    val collections = linkedMapOf<String, IndexCollectionDescription>()
    val codeInjections = arrayListOf<String>()

}

class AssetDescription(id:String) : BaseIndexDescription(id)

class DocumentDescription(id:String) : BaseDocumentDescription(id)

class IndexDescription(id:String) : BaseIndexDescription(id) {

    lateinit var document: String

}

class NestedDocumentDescription(id:String) : BaseDocumentDescription(id)

class DomainMetaRegistry{
    val enums = linkedMapOf<String, DomainEnumDescription>()

    val documents = linkedMapOf<String, DocumentDescription>()

    val nestedDocuments = linkedMapOf<String, NestedDocumentDescription>()

    val indexes = linkedMapOf<String, IndexDescription>()

    val assets = linkedMapOf<String, AssetDescription>()

    companion object {
        fun get(): DomainMetaRegistry {
            return Environment.getPublished(DomainMetaRegistry::class)
        }
    }
}


