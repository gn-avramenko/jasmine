/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxSelectVariantDialogVS():com.gridnine.jasmine.server.core.model.ui.BaseVSEntity(){

    constructor(init: SandboxSelectVariantDialogVS.() ->Unit):this(){
         this.init()
    }

    var variantSelect:com.gridnine.jasmine.server.core.model.ui.SelectConfiguration?=null

    override fun getValue(propertyName: String): Any?{

        if("variantSelect" == propertyName){
            return this.variantSelect
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variantSelect" == propertyName){
            this.variantSelect=value as com.gridnine.jasmine.server.core.model.ui.SelectConfiguration?
            return
        }

        super.setValue(propertyName, value)
    }
}