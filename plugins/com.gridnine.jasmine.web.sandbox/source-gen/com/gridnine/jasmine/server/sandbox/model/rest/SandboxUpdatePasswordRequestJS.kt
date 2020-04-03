/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxUpdatePasswordRequestJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var login:String?=null

    lateinit var model:com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVMJS

    override fun getValue(propertyName: String): Any?{

        if("login" == propertyName){
            return this.login
        }

        if("model" == propertyName){
            return this.model
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("login" == propertyName){
            this.login=value as String?
            return
        }

        if("model" == propertyName){
            this.model=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVMJS
            return
        }

        super.setValue(propertyName, value)
    }
}