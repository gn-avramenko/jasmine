/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SimpleWorkspaceCriterionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceCriterionDTJS(){

    var property:String?=null

    var condition:com.gridnine.jasmine.server.standard.model.rest.WorkspaceSimpleCriterionConditionDTJS?=null

    var value:com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS?=null

    override fun getValue(propertyName: String): Any?{

        if("property" == propertyName){
            return this.property
        }

        if("condition" == propertyName){
            return this.condition
        }

        if("value" == propertyName){
            return this.value
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("property" == propertyName){
            this.property=value as String?
            return
        }

        if("condition" == propertyName){
            this.condition=value as com.gridnine.jasmine.server.standard.model.rest.WorkspaceSimpleCriterionConditionDTJS?
            return
        }

        if("value" == propertyName){
            this.value=value as com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS?
            return
        }

        super.setValue(propertyName, value)
    }
}