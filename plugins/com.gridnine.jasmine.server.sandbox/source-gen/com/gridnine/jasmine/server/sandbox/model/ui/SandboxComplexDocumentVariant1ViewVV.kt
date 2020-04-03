/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariant1ViewVV():com.gridnine.jasmine.server.core.model.ui.BaseVVEntity(){

    var nameValue:String?=null

    var intValue:String?=null

    override fun getValue(propertyName: String): Any?{

        if("nameValue" == propertyName){
            return this.nameValue
        }

        if("intValue" == propertyName){
            return this.intValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("nameValue" == propertyName){
            this.nameValue=value as String?
            return
        }

        if("intValue" == propertyName){
            this.intValue=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}