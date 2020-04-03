/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class ListFilterFloatIntervalValueDT():com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDT(){

    var fromValue:java.math.BigDecimal?=null

    var toValue:java.math.BigDecimal?=null

    override fun getValue(propertyName: String): Any?{

        if("fromValue" == propertyName){
            return this.fromValue
        }

        if("toValue" == propertyName){
            return this.toValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("fromValue" == propertyName){
            this.fromValue=value as java.math.BigDecimal?
            return
        }

        if("toValue" == propertyName){
            this.toValue=value as java.math.BigDecimal?
            return
        }

        super.setValue(propertyName, value)
    }
}