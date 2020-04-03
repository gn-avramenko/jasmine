/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxSelectVariantDialogVSJS():com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS(){

    var variantSelect:com.gridnine.jasmine.web.core.model.ui.SelectConfigurationJS?=null

    override fun getValue(propertyName: String): Any?{

        if("variantSelect" == propertyName){
            return this.variantSelect
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variantSelect" == propertyName){
            this.variantSelect=value as com.gridnine.jasmine.web.core.model.ui.SelectConfigurationJS?
            return
        }

        super.setValue(propertyName, value)
    }
}