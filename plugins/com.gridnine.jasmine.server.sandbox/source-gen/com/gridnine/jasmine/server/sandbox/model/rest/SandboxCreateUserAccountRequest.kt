/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxCreateUserAccountRequest():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    lateinit var model:com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVM

    override fun getValue(propertyName: String): Any?{

        if("model" == propertyName){
            return this.model
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("model" == propertyName){
            this.model=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVM
            return
        }

        super.setValue(propertyName, value)
    }
}