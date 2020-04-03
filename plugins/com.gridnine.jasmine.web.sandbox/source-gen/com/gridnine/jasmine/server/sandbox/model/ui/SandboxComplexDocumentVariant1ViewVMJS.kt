/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariant1ViewVMJS():com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS(){

    var caption:String?=null

    var nameValue:String?=null

    var intValue:Int?=null

    override fun getValue(propertyName: String): Any?{

        if("caption" == propertyName){
            return this.caption
        }

        if("nameValue" == propertyName){
            return this.nameValue
        }

        if("intValue" == propertyName){
            return this.intValue
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

        if("intValue" == propertyName){
            this.intValue=value as Int?
            return
        }

        super.setValue(propertyName, value)
    }
}