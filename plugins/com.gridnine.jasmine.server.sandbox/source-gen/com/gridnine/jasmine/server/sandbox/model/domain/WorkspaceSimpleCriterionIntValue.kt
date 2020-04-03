/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class WorkspaceSimpleCriterionIntValue():com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceSimpleCriterionValue(){

    var value:Int?=null

    override fun getValue(propertyName: String): Any?{

        if("value" == propertyName){
            return this.value
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("value" == propertyName){
            this.value=value as Int?
            return
        }

        super.setValue(propertyName, value)
    }
}