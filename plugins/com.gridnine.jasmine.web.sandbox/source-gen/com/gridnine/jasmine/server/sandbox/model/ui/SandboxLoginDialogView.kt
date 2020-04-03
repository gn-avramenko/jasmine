/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxLoginDialogView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVVJS>(){

    lateinit var login:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var password:com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget

    override fun getValue(propertyName: String): Any?{

        if("login" == propertyName){
            return this.login
        }

        if("password" == propertyName){
            return this.password
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("login" == propertyName){
            this.login=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        if("password" == propertyName){
            this.password=value as com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget
            return
        }

        super.setValue(propertyName, value)
    }
}