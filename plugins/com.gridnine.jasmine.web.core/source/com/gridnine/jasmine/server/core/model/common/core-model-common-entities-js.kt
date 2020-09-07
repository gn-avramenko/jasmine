/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.web.core.utils.MiscUtilsJS


abstract class BaseIdentityJS : BaseIntrospectableObjectJS(){

    open var uid:String =MiscUtilsJS.createUUID()

    override fun equals(other: Any?): Boolean {
        if(other is BaseIdentityJS){
            return other::class == this::class && other.uid == this.uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if (BaseIdentityJS.uid == propertyName) {
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (BaseIdentityJS.uid == propertyName) {
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val uid = "uid"
    }

}


abstract class BaseIntrospectableObjectJS {

    open fun getValue(propertyName: String): Any? {
        throw XeptionJS.forDeveloper("no property with id $propertyName")
    }

    open fun getCollection(collectionName: String): MutableList<Any> {
        throw XeptionJS.forDeveloper("no collection with id $collectionName")
    }

    open fun getMap(mapName: String): MutableMap<Any?,Any?> {
        throw XeptionJS.forDeveloper("no map with id $mapName")
    }

    open fun setValue(propertyName: String, value: Any?) {
        throw XeptionJS.forDeveloper("no property with id $propertyName")
    }

}




class XeptionJS(val type:XeptionTypeJS, val userMessage:String?,  val developerMessage:String?,exception:Exception?) : Exception(getExceptionMessage(userMessage, developerMessage), exception){

    companion object{
        private fun getExceptionMessage(userMessage: String?,  developerMessage: String?): String? {
            if(developerMessage != null){
                return developerMessage
            }
            return null
        }
        fun forDeveloper(message:String, exception: Exception?=null) = XeptionJS(XeptionTypeJS.FOR_DEVELOPER, null, message,  exception)
    }
}




enum class XeptionTypeJS {
    FOR_END_USER,
    FOR_DEVELOPER
}