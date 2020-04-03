/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxCreateUserAccountRequestJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var model:com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVMJS

    override fun getValue(propertyName: String): Any?{

        if("model" == propertyName){
            return this.model
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("model" == propertyName){
            this.model=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVMJS
            return
        }

        super.setValue(propertyName, value)
    }
}