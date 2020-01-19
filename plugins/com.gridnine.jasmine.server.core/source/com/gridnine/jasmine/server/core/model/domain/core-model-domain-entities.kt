/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import java.time.LocalDateTime
import kotlin.reflect.KClass


abstract class BaseAsset : BaseEntity() {


    var created: LocalDateTime? = null

    var createdBy: String? = null

    var modified: LocalDateTime? = null

    var modifiedBy: String? = null



    override fun setValue(propertyName: String, value: Any?) {

        if (BaseAsset.created == propertyName) {
            created = value as LocalDateTime?
            return
        }
        if (BaseAsset.createdBy == propertyName) {
            createdBy = value as String?
            return
        }
        if (BaseAsset.modified == propertyName) {
            modified = value as LocalDateTime?
            return
        }
        if (BaseAsset.modifiedBy == propertyName) {
            modifiedBy = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (BaseAsset.created== propertyName) {
            return created
        }
        if (BaseAsset.createdBy == propertyName) {
            return createdBy
        }
        if (BaseAsset.modified == propertyName) {
            return modified
        }
        if (BaseAsset.modifiedBy == propertyName) {
            return modifiedBy
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val created = "created"
        const val createdBy ="createdBy"
        const val modified ="modified"
        const val modifiedBy = "modifiedBy"
    }

}

abstract class BaseDocument : BaseEntity() {

    var created: LocalDateTime? = null

    var createdBy: String? = null

    var modified: LocalDateTime? = null

    var modifiedBy: String? = null



    override fun setValue(propertyName: String, value: Any?) {

        if (BaseDocument.created == propertyName) {
            created = value as LocalDateTime
            return
        }
        if (BaseDocument.createdBy == propertyName) {
            createdBy = value as String
            return
        }
        if (BaseDocument.modified == propertyName) {
            modified = value as LocalDateTime
            return
        }
        if (BaseDocument.modifiedBy == propertyName) {
            modifiedBy = value as String
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (BaseDocument.created== propertyName) {
            return created
        }
        if (BaseDocument.createdBy == propertyName) {
            return createdBy
        }
        if (BaseDocument.modified == propertyName) {
            return modified
        }
        if (BaseDocument.modifiedBy == propertyName) {
            modifiedBy
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val created = "created"
        const val createdBy ="createdBy"
        const val modified ="modified"
        const val modifiedBy = "modifiedBy"
    }
}

open class EntityReference<D : BaseEntity>():BaseEntity() {


    var caption: String? = null

    lateinit var type: KClass<D>


    constructor(type: KClass<D>, uid: String, caption: String?):this() {
        this.uid = uid
        this.type = type
        this.caption = caption
    }

    override fun toString(): String {
        return caption?:""
    }

    override fun equals(other: Any?): Boolean {
        if (other is EntityReference<*>) {
            return type == other.type && other.uid == uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    companion object{
        const val type = "type"
        const val caption ="caption"
    }
}

abstract class BaseIndex<D : BaseDocument> : BaseEntity() {

    var document: EntityReference<D>? = null

    var navigationKey: String? = null



    override fun getValue(propertyName: String): Any? {
        if (BaseIndex.document == propertyName) {
            return document
        }
        if (BaseIndex.navigationKey == propertyName) {
            return navigationKey
        }
        return  super.getValue(propertyName)
    }


    @Suppress("UNCHECKED_CAST")
    override fun setValue(propertyName: String, value: Any?) {
        if (BaseIndex.document == propertyName) {
            document = value as EntityReference<D>
            return
        }
        if (BaseIndex.navigationKey == propertyName) {
            navigationKey = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport


    companion object{
        const val document= "document"
        const val navigationKey="navigationKey"
        val referenceCaption = _TestDomainDocumentIndexProperty0("documentCaption")
    }
}
abstract class BaseNestedDocument : BaseEntity()





