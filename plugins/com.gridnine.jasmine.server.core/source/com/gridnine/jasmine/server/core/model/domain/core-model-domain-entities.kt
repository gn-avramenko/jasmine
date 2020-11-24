/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.server.core.model.domain

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import kotlin.reflect.KClass


abstract class BaseAsset : BaseIdentity() {

    private var revision:Int =0

    override fun setValue(propertyName: String, value: Any?) {

        if (BaseAsset.revision == propertyName) {
            revision = value as Int
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (BaseAsset.revision == propertyName) {
            return revision
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val revision = "revision"
    }

}

abstract class BaseDocument : BaseIdentity() {

    private var revision:Int =0

    override fun setValue(propertyName: String, value: Any?) {

        if (BaseDocument.revision == propertyName) {
            revision = value as Int
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (BaseDocument.revision == propertyName) {
            return revision
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val revision = "revision"
    }
}

open class ObjectReference<D : BaseIdentity>():BaseIdentity() {

    open var caption: String? = null

    open lateinit var type: KClass<D>

    constructor(type: KClass<D>, uid: String, caption: String?):this() {
        this.uid = uid
        this.type = type
        this.caption = caption
    }

    override fun toString(): String {
        return caption?:""
    }

    override fun equals(other: Any?): Boolean {
        if (other is ObjectReference<*>) {
            return type == other.type && other.uid == uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if(ObjectReference.type == propertyName){
            return type
        }
        if(ObjectReference.caption == propertyName){
            return caption
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(ObjectReference.type == propertyName){
            type = value as KClass<D>
            return
        }
        if(ObjectReference.caption == propertyName){
            caption = value as String?
            return
        }
        super.setValue(propertyName, value)
    }
    companion object{
        const val type = "type"
        const val caption ="caption"
    }
}





abstract class BaseIndex<D : BaseDocument> : BaseIdentity() {

    var document: ObjectReference<D>? = null


    override fun getValue(propertyName: String): Any? {
        if (BaseIndex.documentField == propertyName) {
            return document
        }
        return  super.getValue(propertyName)
    }


    @Suppress("UNCHECKED_CAST")
    override fun setValue(propertyName: String, value: Any?) {
        if (BaseIndex.documentField == propertyName) {
            document = value as ObjectReference<D>
            return
        }
        super.setValue(propertyName, value)
    }

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport


    companion object{
        const val documentField= "document"
        val referenceCaptionField = _TestDomainDocumentIndexProperty0("documentCaption")
    }
}
abstract class BaseNestedDocument : BaseIdentity()


object EntityUtils{
    fun<D:BaseIdentity> toReference(doc:D):ObjectReference<D>{
        return ObjectReference(doc::class as KClass<D>, doc.uid, doc.toString())
    }
}



