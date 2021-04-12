/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper

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
}

class DocumentCollectionDescription(id: String) : BaseModelElementDescription(id){
    var elementClassName: String? = null
    lateinit var elementType: DocumentPropertyType
    var unique = false

}


class DomainEnumItemDescription(id:String) : BaseModelElementDescription(id)


class DomainEnumDescription(id:String) : BaseModelElementDescription(id){
    val items = linkedMapOf<String, DomainEnumItemDescription>()
}

abstract class BaseDocumentDescription(id:String) : BaseModelElementDescription(id) {

    val properties = linkedMapOf<String, DocumentPropertyDescription>()

    val collections = linkedMapOf<String, DocumentCollectionDescription>()

    val codeInjections = arrayListOf<String>()

    var isAbstract=  false

    var extendsId: String? = null

}

class IndexPropertyDescription(id:String) : BaseModelElementDescription(id) {
    var nonNullable = false
    var className: String? = null
    lateinit var type: DatabasePropertyType

}

class IndexCollectionDescription(id:String) : BaseModelElementDescription(id) {

    var elementClassName: String? = null
    lateinit var elementType: DatabaseCollectionType
    var unique = false

}
abstract class BaseIndexDescription(id:String) : BaseModelElementDescription(id) {
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

class DomainMetaRegistry: Disposable {
    val enums = linkedMapOf<String, DomainEnumDescription>()

    val documents = linkedMapOf<String, DocumentDescription>()

    val nestedDocuments = linkedMapOf<String, NestedDocumentDescription>()

    val indexes = linkedMapOf<String, IndexDescription>()

    val assets = linkedMapOf<String, AssetDescription>()


    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(DomainMetaRegistry::class)
        fun get() = wrapper.get()
        const val EXPOSED_IN_REST_KEY = "x-exposed-in-rest"
    }
}


