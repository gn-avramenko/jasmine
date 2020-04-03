/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class WorkspaceSimpleCriterionDateIntervalValue():com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceSimpleCriterionValue(){

    var startDate:java.time.LocalDate?=null

    var endDate:java.time.LocalDate?=null

    override fun getValue(propertyName: String): Any?{

        if("startDate" == propertyName){
            return this.startDate
        }

        if("endDate" == propertyName){
            return this.endDate
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("startDate" == propertyName){
            this.startDate=value as java.time.LocalDate?
            return
        }

        if("endDate" == propertyName){
            this.endDate=value as java.time.LocalDate?
            return
        }

        super.setValue(propertyName, value)
    }
}