/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EnumDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    val items = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("items" == collectionName){
            return this.items as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}