/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxUpdatePasswordRequest():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var login:String?=null

    lateinit var model:com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVM

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
            this.model=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVM
            return
        }

        super.setValue(propertyName, value)
    }
}