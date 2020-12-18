/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused","UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.BaseMetaElementDescriptionJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS


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

enum class DatabaseCollectionTypeJS {
    STRING,
    ENUM,
    ENTITY_REFERENCE
}




class DomainEnumItemDescriptionJS(id:String,val displayName:String) : BaseMetaElementDescriptionJS(id)


class DomainEnumDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id){
    val items = linkedMapOf<String, DomainEnumItemDescriptionJS>()
}

class DocumentPropertyDescriptionJS(id: String, val type:DocumentPropertyTypeJS, val className: String?, val nonNullable: Boolean) : BaseMetaElementDescriptionJS(id)


class DocumentCollectionDescriptionJS(id: String,val elementType: DocumentPropertyTypeJS, val elementClassName: String?) : BaseMetaElementDescriptionJS(id)

class DocumentDescriptionJS(id:String, val isAbstract:Boolean, val extendsId:String?, val root:Boolean) : BaseMetaElementDescriptionJS(id) {

    val properties = linkedMapOf<String, DocumentPropertyDescriptionJS>()

    val collections = linkedMapOf<String, DocumentCollectionDescriptionJS>()


}

class IndexPropertyDescriptionJS(id:String,val type:DatabasePropertyTypeJS, val className:String?, val displayName: String, val nonNullable:Boolean) : BaseMetaElementDescriptionJS(id)

class IndexCollectionDescriptionJS(id:String, val elementType:DatabaseCollectionTypeJS, val elementClassName: String?,
 val unique:Boolean, val displayName: String) : BaseMetaElementDescriptionJS(id)
abstract class BaseIndexDescriptionJS(id:String,  val displayName: String) : BaseMetaElementDescriptionJS(id) {
    val properties = linkedMapOf<String, IndexPropertyDescriptionJS>()
    val collections = linkedMapOf<String, IndexCollectionDescriptionJS>()
}

class AssetDescriptionJS(id:String, displayName: String) : BaseIndexDescriptionJS(id,displayName)


class IndexDescriptionJS(id:String, displayName: String, val document:String) : BaseIndexDescriptionJS(id,displayName)


class DomainMetaRegistryJS{
    val enums = linkedMapOf<String, DomainEnumDescriptionJS>()

    val indexes = linkedMapOf<String, IndexDescriptionJS>()

    val documents = linkedMapOf<String, DocumentDescriptionJS>()

    val assets = linkedMapOf<String, AssetDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(DomainMetaRegistryJS::class)
    }
}


