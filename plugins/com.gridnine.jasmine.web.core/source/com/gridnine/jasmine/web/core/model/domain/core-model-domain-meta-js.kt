/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.web.core.model.domain

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.common.BaseIdentityDescriptionJS

enum class DatabasePropertyTypeJS  {
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



enum class DatabaseCollectionTypeJS  {
    STRING,
    ENUM,
    ENTITY_REFERENCE
}



class DomainEnumItemDescriptionJS(id:String, val displayName:String) : BaseIdentityDescriptionJS(id)

class DomainEnumDescriptionJS(id:String) : BaseIdentityDescriptionJS(id){
    val items = linkedMapOf<String, DomainEnumItemDescriptionJS>()
}

class DatabasePropertyDescriptionJS(id:String,val type: DatabasePropertyTypeJS, val displayName: String) : BaseIdentityDescriptionJS(id) {
    var notNullable = false
    var className: String? = null
}

class DatabaseCollectionDescriptionJS(id:String, val elementType: DatabaseCollectionTypeJS, val displayName: String) : BaseIdentityDescriptionJS(id) {
    var elementClassName: String? = null

}
abstract class BaseIndexDescriptionJS(id:String, val displayName: String) : BaseIdentityDescriptionJS(id) {
    val properties = linkedMapOf<String, DatabasePropertyDescriptionJS>()
    val collections = linkedMapOf<String, DatabaseCollectionDescriptionJS>()
    val codeInjections = arrayListOf<String>()

}

class AssetDescriptionJS(id:String, displayName: String) : BaseIndexDescriptionJS(id,displayName)


class IndexDescriptionJS(id:String, displayName: String) : BaseIndexDescriptionJS(id,displayName) {

    lateinit var document: String

}

class DomainMetaRegistryJS{
    val enums = linkedMapOf<String, DomainEnumDescriptionJS>()

    val indexes = linkedMapOf<String, IndexDescriptionJS>()

    val assets = linkedMapOf<String, AssetDescriptionJS>()

    companion object {
        fun get(): DomainMetaRegistryJS {
            return EnvironmentJS.getPublished(DomainMetaRegistryJS::class)
        }
    }
}


