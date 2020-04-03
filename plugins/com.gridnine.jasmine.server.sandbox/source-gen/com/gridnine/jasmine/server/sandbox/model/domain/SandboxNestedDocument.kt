/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxNestedDocument():com.gridnine.jasmine.server.core.model.domain.BaseNestedDocument(){

    var textColumn:String?=null

    var enumColumn:com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?=null

    var entityRefColumn:com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?=null

    var integerColumn:Int?=null

    var floatColumn:java.math.BigDecimal?=null

    override fun getValue(propertyName: String): Any?{

        if("textColumn" == propertyName){
            return this.textColumn
        }

        if("enumColumn" == propertyName){
            return this.enumColumn
        }

        if("entityRefColumn" == propertyName){
            return this.entityRefColumn
        }

        if("integerColumn" == propertyName){
            return this.integerColumn
        }

        if("floatColumn" == propertyName){
            return this.floatColumn
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("textColumn" == propertyName){
            this.textColumn=value as String?
            return
        }

        if("enumColumn" == propertyName){
            this.enumColumn=value as com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?
            return
        }

        if("entityRefColumn" == propertyName){
            this.entityRefColumn=value as com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?
            return
        }

        if("integerColumn" == propertyName){
            this.integerColumn=value as Int?
            return
        }

        if("floatColumn" == propertyName){
            this.floatColumn=value as java.math.BigDecimal?
            return
        }

        super.setValue(propertyName, value)
    }
}