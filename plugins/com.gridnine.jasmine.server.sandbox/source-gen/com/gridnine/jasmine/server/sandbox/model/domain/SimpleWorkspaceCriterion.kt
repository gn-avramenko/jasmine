/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SimpleWorkspaceCriterion():com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceCriterion(){

    var property:String?=null

    var condition:com.gridnine.jasmine.server.sandbox.model.domain.WorkspaceSimpleCriterionCondition?=null

    var value:com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceSimpleCriterionValue?=null

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
            this.condition=value as com.gridnine.jasmine.server.sandbox.model.domain.WorkspaceSimpleCriterionCondition?
            return
        }

        if("value" == propertyName){
            this.value=value as com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceSimpleCriterionValue?
            return
        }

        super.setValue(propertyName, value)
    }
}