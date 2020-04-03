/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class GetListResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var totalCount:Long?=null

    val items = arrayListOf<com.gridnine.jasmine.web.core.model.common.BaseEntityJS>()

    override fun getValue(propertyName: String): Any?{

        if("totalCount" == propertyName){
            return this.totalCount
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("totalCount" == propertyName){
            this.totalCount=value as Long?
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