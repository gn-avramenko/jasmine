/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class ListFilterFloatIntervalValueDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS(){

    var fromValue:Double?=null

    var toValue:Double?=null

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
            this.fromValue=value as Double?
            return
        }

        if("toValue" == propertyName){
            this.toValue=value as Double?
            return
        }

        super.setValue(propertyName, value)
    }
}