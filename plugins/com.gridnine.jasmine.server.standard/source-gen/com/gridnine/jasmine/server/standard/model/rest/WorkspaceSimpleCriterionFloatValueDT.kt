/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceSimpleCriterionFloatValueDT():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDT(){

    var value:java.math.BigDecimal?=null

    override fun getValue(propertyName: String): Any?{

        if("value" == propertyName){
            return this.value
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("value" == propertyName){
            this.value=value as java.math.BigDecimal?
            return
        }

        super.setValue(propertyName, value)
    }
}