/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class VMEntityDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    val properties = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.VMPropertyDescriptionDTJS>()

    val collections = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.VMCollectionDescriptionDTJS>()

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

        if("properties" == collectionName){
            return this.properties as MutableCollection<Any>
        }

        if("collections" == collectionName){
            return this.collections as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}