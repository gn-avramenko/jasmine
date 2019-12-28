/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.web.core.model.domain

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS


abstract class BaseAssetJS : BaseEntityJS()

open class EntityReferenceJS : BaseEntityJS {

    var caption: String? = null

    lateinit var type: String

    constructor()

    constructor(type: String, uid: String, caption: String?) {
        this.type = type
        this.uid = uid
        this.caption = caption
    }

    override fun toString(): String {
        return caption?:""
    }

    override fun equals(other: Any?): Boolean {
        if (other is EntityReferenceJS) {
            return uid == other.uid
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return if (uid == null) 0 else uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if(Companion.type == propertyName){
            return type
        }
        if(Companion.caption == propertyName){
            return caption
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(Companion.type == propertyName){
            type = value as String
            return
        }
        if(Companion.caption == propertyName){
            caption = value as String
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val type = "type"
        const val caption = "caption"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS"
    }

}

abstract class BaseIndexJS : BaseEntityJS() {

    lateinit var document: EntityReferenceJS


    override fun getValue(propertyName: String): Any? {
        if (Companion.document == propertyName) {
            return document
        }
        return super.getValue(propertyName)
    }


    override fun setValue(propertyName: String, value: Any?) {
        if (Companion.document == propertyName) {
            document = value as EntityReferenceJS
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val document="document"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.domain.BaseIndexJS"
    }
}





