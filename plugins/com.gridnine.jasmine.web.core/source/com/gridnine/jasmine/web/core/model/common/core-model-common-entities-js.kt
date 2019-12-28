/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.web.core.model.common


abstract class BaseEntityJS : BaseIntrospectableObjectJS(){


    var uid:String? = null


    override fun equals(other: Any?): Boolean {
        if(other is BaseEntityJS){
            return other::class == this::class && other.uid == this.uid
        }
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if (BaseEntityJS.uid == propertyName) {
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (BaseEntityJS.uid == propertyName) {
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }


    companion object{
        const val uid = "uid"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.common.BaseEntityJS"
    }

}


abstract class BaseIntrospectableObjectJS {


    open fun getValue(propertyName: String): Any? {
        throw IllegalArgumentException("no property with id $propertyName")
    }

    open fun getCollection(collectionName: String): MutableCollection<Any> {
        throw IllegalArgumentException("no collection with id $collectionName")
    }

    open fun setValue(propertyName: String, value: Any?) {
        throw IllegalArgumentException("no property with id $propertyName")
    }

}

