/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceGroupDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var displayName:String?=null

    val items = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceItemDT>()

    override fun getValue(propertyName: String): Any?{

        if("displayName" == propertyName){
            return this.displayName
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("displayName" == propertyName){
            this.displayName=value as String?
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