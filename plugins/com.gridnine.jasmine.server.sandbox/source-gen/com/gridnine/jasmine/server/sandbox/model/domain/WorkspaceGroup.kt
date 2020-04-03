/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class WorkspaceGroup():com.gridnine.jasmine.server.core.model.domain.BaseNestedDocument(){

    var displayName:String?=null

    val items = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceItem>()

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