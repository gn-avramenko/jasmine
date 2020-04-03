/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceSimpleCriterionDateTimeIntervalValueDT():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDT(){

    var startDate:java.time.LocalDateTime?=null

    var endDate:java.time.LocalDateTime?=null

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
            this.startDate=value as java.time.LocalDateTime?
            return
        }

        if("endDate" == propertyName){
            this.endDate=value as java.time.LocalDateTime?
            return
        }

        super.setValue(propertyName, value)
    }
}