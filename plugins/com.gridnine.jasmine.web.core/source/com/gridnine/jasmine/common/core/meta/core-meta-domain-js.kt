/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.web.core.common.EnvironmentJS

enum class DocumentPropertyTypeJS {
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

enum class DatabasePropertyTypeJS {
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



enum class DatabaseCollectionTypeJS {
    STRING,
    ENUM,
    ENTITY_REFERENCE
}


class DocumentPropertyDescriptionJS(id: String) : BaseModelElementDescriptionJS(id){
    var nonNullable = false
    var className: String? = null
    lateinit var type: DocumentPropertyTypeJS
}

class DocumentCollectionDescriptionJS(id: String) : BaseModelElementDescriptionJS(id){
    var elementClassName: String? = null
    lateinit var elementType: DocumentPropertyTypeJS
    var unique = false

}


class DomainEnumItemDescriptionJS(id:String) : BaseModelElementDescriptionJS(id)


class DomainEnumDescriptionJS(id:String) : BaseModelElementDescriptionJS(id){
    val items = linkedMapOf<String, DomainEnumItemDescriptionJS>()
}

abstract class BaseDocumentDescriptionJS(id:String) : BaseModelElementDescriptionJS(id) {

    val properties = linkedMapOf<String, DocumentPropertyDescriptionJS>()

    val collections = linkedMapOf<String, DocumentCollectionDescriptionJS>()

    val codeInjections = arrayListOf<String>()

    var isAbstract=  false

    var extendsId: String? = null

}

class IndexPropertyDescriptionJS(id:String) : BaseModelElementDescriptionJS(id) {
    var nonNullable = false
    var className: String? = null
    lateinit var type: DatabasePropertyTypeJS

}

class IndexCollectionDescriptionJS(id:String) : BaseModelElementDescriptionJS(id) {

    var elementClassName: String? = null
    lateinit var elementType: DatabaseCollectionTypeJS
    var unique = false

}
abstract class BaseIndexDescriptionJS(id:String) : BaseModelElementDescriptionJS(id) {
    val properties = linkedMapOf<String, IndexPropertyDescriptionJS>()
    val collections = linkedMapOf<String, IndexCollectionDescriptionJS>()
    val codeInjections = arrayListOf<String>()
}

class AssetDescriptionJS(id:String) : BaseIndexDescriptionJS(id)


class IndexDescriptionJS(id:String) : BaseIndexDescriptionJS(id) {
    lateinit var document: String
}

class DocumentDescriptionJS(id:String, val isAbstract:Boolean, val extendsId:String?, val root:Boolean) : BaseModelElementDescriptionJS(id) {

    val properties = linkedMapOf<String, DocumentPropertyDescriptionJS>()

    val collections = linkedMapOf<String, DocumentCollectionDescriptionJS>()

}

class DomainMetaRegistryJS {
    val enums = linkedMapOf<String, DomainEnumDescriptionJS>()

    val indexes = linkedMapOf<String, IndexDescriptionJS>()

    val assets = linkedMapOf<String, AssetDescriptionJS>()

    val documents = linkedMapOf<String,DocumentDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(DomainMetaRegistryJS::class)
    }
}


