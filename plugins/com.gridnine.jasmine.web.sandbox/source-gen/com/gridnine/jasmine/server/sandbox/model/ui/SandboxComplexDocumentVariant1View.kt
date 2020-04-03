/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariant1View():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant1ViewVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant1ViewVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant1ViewVVJS>(){

    lateinit var nameValue:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var intValue:com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget

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
            this.nameValue=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        if("intValue" == propertyName){
            this.intValue=value as com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget
            return
        }

        super.setValue(propertyName, value)
    }
}