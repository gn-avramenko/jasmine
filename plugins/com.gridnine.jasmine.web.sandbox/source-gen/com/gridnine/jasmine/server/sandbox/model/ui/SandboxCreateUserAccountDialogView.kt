/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxCreateUserAccountDialogView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVVJS>(){

    lateinit var login:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var password:com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget

    lateinit var passwordRepeat:com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget

    override fun getValue(propertyName: String): Any?{

        if("login" == propertyName){
            return this.login
        }

        if("password" == propertyName){
            return this.password
        }

        if("passwordRepeat" == propertyName){
            return this.passwordRepeat
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

        if("passwordRepeat" == propertyName){
            this.passwordRepeat=value as com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget
            return
        }

        super.setValue(propertyName, value)
    }
}