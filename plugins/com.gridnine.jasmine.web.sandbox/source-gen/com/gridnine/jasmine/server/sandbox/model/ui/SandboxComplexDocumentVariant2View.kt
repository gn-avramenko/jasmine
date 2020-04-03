/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariant2View():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant2ViewVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant2ViewVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariant2ViewVVJS>(){

    lateinit var nameValue:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var dateValue:com.gridnine.jasmine.web.core.model.ui.DateBoxWidget

    override fun getValue(propertyName: String): Any?{

        if("nameValue" == propertyName){
            return this.nameValue
        }

        if("dateValue" == propertyName){
            return this.dateValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("nameValue" == propertyName){
            this.nameValue=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        if("dateValue" == propertyName){
            this.dateValue=value as com.gridnine.jasmine.web.core.model.ui.DateBoxWidget
            return
        }

        super.setValue(propertyName, value)
    }
}