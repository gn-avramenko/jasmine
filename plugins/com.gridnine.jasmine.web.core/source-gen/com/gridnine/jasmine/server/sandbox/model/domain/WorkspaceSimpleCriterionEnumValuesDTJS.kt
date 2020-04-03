/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class WorkspaceSimpleCriterionEnumValuesDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS(){

    var enumClassName:String?=null

    val values = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("enumClassName" == propertyName){
            return this.enumClassName
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("enumClassName" == propertyName){
            this.enumClassName=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("values" == collectionName){
            return this.values as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}