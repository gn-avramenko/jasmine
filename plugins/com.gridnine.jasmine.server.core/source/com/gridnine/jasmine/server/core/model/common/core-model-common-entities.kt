/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common


abstract class BaseIdentity : BaseIntrospectableObject(){

    var uid:String? = null

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
        throw IllegalArgumentException("no property with id $propertyName")
    }

    open fun getCollection(listName: String): MutableList<Any> {
        throw IllegalArgumentException("no collection with id $listName")
    }

    open fun getMap(mapName: String): MutableMap<Any?,Any?> {
        throw IllegalArgumentException("no map with id $mapName")
    }

    open fun setValue(propertyName: String, value: Any?) {
        throw IllegalArgumentException("no property with id $propertyName")
    }

}

