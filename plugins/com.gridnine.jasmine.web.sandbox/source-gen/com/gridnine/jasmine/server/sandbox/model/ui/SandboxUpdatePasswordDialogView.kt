/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxUpdatePasswordDialogView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVVJS>(){

    lateinit var oldPassword:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var password:com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget

    lateinit var passwordRepeat:com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget

    override fun getValue(propertyName: String): Any?{

        if("oldPassword" == propertyName){
            return this.oldPassword
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

        if("oldPassword" == propertyName){
            this.oldPassword=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
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