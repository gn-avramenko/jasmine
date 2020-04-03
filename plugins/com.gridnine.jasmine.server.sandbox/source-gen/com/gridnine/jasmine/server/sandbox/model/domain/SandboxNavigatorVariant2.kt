/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxNavigatorVariant2():com.gridnine.jasmine.server.sandbox.model.domain.BaseSandboxNavigatorVariant(){

    var dateValue:java.time.LocalDate?=null

    override fun getValue(propertyName: String): Any?{

        if("dateValue" == propertyName){
            return this.dateValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("dateValue" == propertyName){
            this.dateValue=value as java.time.LocalDate?
            return
        }

        super.setValue(propertyName, value)
    }
}