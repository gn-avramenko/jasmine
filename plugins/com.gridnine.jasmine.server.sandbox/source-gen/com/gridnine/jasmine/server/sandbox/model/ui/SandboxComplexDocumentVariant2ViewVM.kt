/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariant2ViewVM():com.gridnine.jasmine.server.core.model.ui.BaseVMEntity(){

    var caption:String?=null

    var nameValue:String?=null

    var dateValue:java.time.LocalDate?=null

    override fun getValue(propertyName: String): Any?{

        if("caption" == propertyName){
            return this.caption
        }

        if("nameValue" == propertyName){
            return this.nameValue
        }

        if("dateValue" == propertyName){
            return this.dateValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("caption" == propertyName){
            this.caption=value as String?
            return
        }

        if("nameValue" == propertyName){
            this.nameValue=value as String?
            return
        }

        if("dateValue" == propertyName){
            this.dateValue=value as java.time.LocalDate?
            return
        }

        super.setValue(propertyName, value)
    }
}