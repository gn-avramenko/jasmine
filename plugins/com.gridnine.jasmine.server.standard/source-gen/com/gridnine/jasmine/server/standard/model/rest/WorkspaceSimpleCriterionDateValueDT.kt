/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceSimpleCriterionDateValueDT():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDT(){

    var value:java.time.LocalDate?=null

    override fun getValue(propertyName: String): Any?{

        if("value" == propertyName){
            return this.value
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("value" == propertyName){
            this.value=value as java.time.LocalDate?
            return
        }

        super.setValue(propertyName, value)
    }
}