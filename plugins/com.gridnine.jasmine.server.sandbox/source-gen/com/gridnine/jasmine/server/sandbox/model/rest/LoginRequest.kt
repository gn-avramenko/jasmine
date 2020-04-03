/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class LoginRequest():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var data:com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVM?=null

    override fun getValue(propertyName: String): Any?{

        if("data" == propertyName){
            return this.data
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("data" == propertyName){
            this.data=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVM?
            return
        }

        super.setValue(propertyName, value)
    }
}