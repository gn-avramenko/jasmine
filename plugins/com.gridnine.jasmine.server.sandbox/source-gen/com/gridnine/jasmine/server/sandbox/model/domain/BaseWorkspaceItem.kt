/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

abstract class BaseWorkspaceItem():com.gridnine.jasmine.server.core.model.domain.BaseNestedDocument(){

    var displayName:String?=null

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
}