/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class ListWorkspaceItem():com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceItem(){

    var listId:String?=null

    val criterions = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceCriterion>()

    val columns = arrayListOf<String>()

    val filters = arrayListOf<String>()

    val sortOrders = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.SortOrder>()

    override fun getValue(propertyName: String): Any?{

        if("listId" == propertyName){
            return this.listId
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("listId" == propertyName){
            this.listId=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("criterions" == collectionName){
            return this.criterions as MutableCollection<Any>
        }

        if("columns" == collectionName){
            return this.columns as MutableCollection<Any>
        }

        if("filters" == collectionName){
            return this.filters as MutableCollection<Any>
        }

        if("sortOrders" == collectionName){
            return this.sortOrders as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}