/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.server.core.model.l10n.L10nMetaregistry
import java.util.*


abstract class BaseIdentity : BaseIntrospectableObject(){

    open var uid:String =UUID.randomUUID().toString()

    override fun equals(other: Any?): Boolean {
        if(other is BaseIdentity){
            return other::class == this::class && other.uid == this.uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if (BaseIdentity.uid == propertyName) {
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (BaseIdentity.uid == propertyName) {
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val uid = "uid"
    }

}


abstract class BaseIntrospectableObject {

    open fun getValue(propertyName: String): Any? {
        throw Xeption.forDeveloper("no property with id $propertyName")
    }

    open fun getCollection(collectionName: String): MutableList<Any> {
        throw Xeption.forDeveloper("no collection with id $collectionName")
    }

    open fun getMap(mapName: String): MutableMap<Any?,Any?> {
        throw Xeption.forDeveloper("no map with id $mapName")
    }

    open fun setValue(propertyName: String, value: Any?) {
        throw Xeption.forDeveloper("no property with id $propertyName")
    }

}


class L10nMessage: BaseIntrospectableObject {
    lateinit var key:String
    val parameters = arrayListOf<Any>()

    constructor()
    constructor(key:String, vararg parameters:Any){
        this.key = key
        parameters.forEach {this.parameters.add(it)}
    }

    override fun getCollection(collectionName: String): MutableList<Any> {
        if(Companion.parameters == collectionName){
            return parameters
        }
        return super.getCollection(collectionName)
    }

    override fun getValue(propertyName: String): Any? {
        if(Companion.key == propertyName){
            return key
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(Companion.key == propertyName){
            key = value as String
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val key ="key"
        const val parameters = "parameters"
    }

    override fun toString(): String {
        val md = L10nMetaregistry.get().messages[this.key]
        var result = md?.getDisplayName()?:this.key
        this.parameters.withIndex().forEach{(idx, value) ->
            result = result.replace("{$idx}", value.toString())
        }
        return result
    }
}

class Xeption(val type:XeptionType, val userMessage:L10nMessage?, val adminMessage:L10nMessage?, val developerMessage:String?,exception:Exception?) : Exception(getExceptionMessage(userMessage, adminMessage, developerMessage), exception){

    companion object{
        private fun getExceptionMessage(userMessage: L10nMessage?, adminMessage: L10nMessage?, developerMessage: String?): String? {
            if(developerMessage != null){
                return developerMessage
            }
            return null
        }
        fun forDeveloper(message:String, exception: Exception?=null) = Xeption(XeptionType.FOR_DEVELOPER, null, null, message, exception)
        fun forAdmin(message:L10nMessage, exception: Exception?=null) = Xeption(XeptionType.FOR_ADMIN, null, message, null, exception)
    }
}




enum class XeptionType {
    FOR_END_USER,
    FOR_ADMIN,
    FOR_DEVELOPER
}