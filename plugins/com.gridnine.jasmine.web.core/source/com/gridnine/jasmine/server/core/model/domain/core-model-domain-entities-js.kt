/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.BaseIdentityJS


abstract class BaseAssetJS : BaseIdentityJS()

abstract class BaseDocumentJS : BaseIdentityJS() {

    private var revision:Int =0

    override fun setValue(propertyName: String, value: Any?) {

        if (BaseDocumentJS.revision == propertyName) {
            revision = value as Int
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (BaseDocumentJS.revision == propertyName) {
            return revision
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val revision = "revision"
        const val qualifiedClassName = "com.gridnine.jasmine.server.core.model.domain.BaseDocumentJS"
    }
}

open class ObjectReferenceJS():BaseIdentityJS() {

    open var caption: String? = null

    open lateinit var type: String

    constructor(type: String, uid: String, caption: String?):this() {
        this.uid = uid
        this.type = type
        this.caption = caption
    }

    override fun toString(): String {
        return caption?:""
    }

    override fun equals(other: Any?): Boolean {
        if (other is ObjectReferenceJS) {
            return type == other.type && other.uid == uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if(ObjectReferenceJS.type == propertyName){
            return type
        }
        if(ObjectReferenceJS.caption == propertyName){
            return caption
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(ObjectReferenceJS.type == propertyName){
            type = value as String
            if(!type.endsWith("JS")){
                type += "JS"
            }
            return
        }
        if(ObjectReferenceJS.caption == propertyName){
            caption = value as String?
            return
        }
        super.setValue(propertyName, value)
    }
    companion object{
        const val type = "type"
        const val caption ="caption"
        const val qualifiedClassName = "com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS"
    }
}





abstract class BaseIndexJS : BaseIdentityJS() {

    lateinit var document: ObjectReferenceJS

    override fun getValue(propertyName: String): Any? {
        if (BaseIndexJS.document == propertyName) {
            return document
        }
        return  super.getValue(propertyName)
    }


    @Suppress("UNCHECKED_CAST")
    override fun setValue(propertyName: String, value: Any?) {
        if (BaseIndexJS.document == propertyName) {
            document = value as ObjectReferenceJS
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val document= "document"
    }
}




