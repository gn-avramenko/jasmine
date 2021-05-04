/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.common.core.model

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
        if (Companion.uid == propertyName) {
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (Companion.uid == propertyName) {
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val uid = "uid"
    }

}


@Suppress("CanBeParameter")
class XeptionJS(val type: XeptionTypeJS, val userMessage: String?, val adminMessage: String?, val developerMessage:String?, exception:Exception?) : Exception(getExceptionMessage(userMessage, adminMessage, developerMessage), exception){

    companion object{
        private fun getExceptionMessage(userMessage: String?, adminMessage: String?, developerMessage: String?): String? {
            if(developerMessage != null){
                return developerMessage
            }
            if(adminMessage != null){
                return adminMessage
            }
            if(userMessage != null){
                return userMessage
            }
            return null
        }
        fun forDeveloper(message:String, exception: Exception?=null) = XeptionJS(XeptionTypeJS.FOR_DEVELOPER, null, null, message, exception)
        fun forAdmin(message: String, exception: Exception?=null) = XeptionJS(XeptionTypeJS.FOR_ADMIN, null, message, null, exception)
        fun forEndUser(message: String, exception: Exception?=null) = XeptionJS(XeptionTypeJS.FOR_END_USER, message, null, null, exception)
    }
}




enum class XeptionTypeJS {
    FOR_END_USER,
    FOR_ADMIN,
    FOR_DEVELOPER
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

interface  HasPriorityJS{
    val priority:Double
}

enum class FakeEnumJS