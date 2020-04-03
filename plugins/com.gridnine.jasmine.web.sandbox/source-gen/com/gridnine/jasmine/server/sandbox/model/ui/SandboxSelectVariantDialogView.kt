/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxSelectVariantDialogView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxSelectVariantDialogVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxSelectVariantDialogVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxSelectVariantDialogVVJS>(){

    lateinit var variantSelect:com.gridnine.jasmine.web.core.model.ui.SelectWidget

    override fun getValue(propertyName: String): Any?{

        if("variantSelect" == propertyName){
            return this.variantSelect
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variantSelect" == propertyName){
            this.variantSelect=value as com.gridnine.jasmine.web.core.model.ui.SelectWidget
            return
        }

        super.setValue(propertyName, value)
    }
}