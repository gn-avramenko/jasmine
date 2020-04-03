/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EntityAutocompleteRequestJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var searchText:String?=null

    var limit:Int?=null

    val entitiesIds = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("searchText" == propertyName){
            return this.searchText
        }

        if("limit" == propertyName){
            return this.limit
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("searchText" == propertyName){
            this.searchText=value as String?
            return
        }

        if("limit" == propertyName){
            this.limit=value as Int?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("entitiesIds" == collectionName){
            return this.entitiesIds as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}