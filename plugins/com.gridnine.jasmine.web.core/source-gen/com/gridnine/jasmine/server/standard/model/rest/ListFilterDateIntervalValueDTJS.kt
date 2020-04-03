/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class ListFilterDateIntervalValueDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS(){

    var startDate:kotlin.js.Date?=null

    var endDate:kotlin.js.Date?=null

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
            this.startDate=value as kotlin.js.Date?
            return
        }

        if("endDate" == propertyName){
            this.endDate=value as kotlin.js.Date?
            return
        }

        super.setValue(propertyName, value)
    }
}