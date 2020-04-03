/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxTableVM():com.gridnine.jasmine.server.core.model.ui.BaseVMEntity(){

    var textColumn:String?=null

    var entityColumn:com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?=null

    var enumColumn:com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?=null

    var floatColumn:java.math.BigDecimal?=null

    var integerColumn:Int?=null

    override fun getValue(propertyName: String): Any?{

        if("textColumn" == propertyName){
            return this.textColumn
        }

        if("entityColumn" == propertyName){
            return this.entityColumn
        }

        if("enumColumn" == propertyName){
            return this.enumColumn
        }

        if("floatColumn" == propertyName){
            return this.floatColumn
        }

        if("integerColumn" == propertyName){
            return this.integerColumn
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("textColumn" == propertyName){
            this.textColumn=value as String?
            return
        }

        if("entityColumn" == propertyName){
            this.entityColumn=value as com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?
            return
        }

        if("enumColumn" == propertyName){
            this.enumColumn=value as com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?
            return
        }

        if("floatColumn" == propertyName){
            this.floatColumn=value as java.math.BigDecimal?
            return
        }

        if("integerColumn" == propertyName){
            this.integerColumn=value as Int?
            return
        }

        super.setValue(propertyName, value)
    }
}