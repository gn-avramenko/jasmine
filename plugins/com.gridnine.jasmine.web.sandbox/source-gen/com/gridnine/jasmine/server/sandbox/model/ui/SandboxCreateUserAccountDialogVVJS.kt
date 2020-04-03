/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxCreateUserAccountDialogVVJS():com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS(){

    var login:String?=null

    var password:String?=null

    var passwordRepeat:String?=null

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
            this.login=value as String?
            return
        }

        if("password" == propertyName){
            this.password=value as String?
            return
        }

        if("passwordRepeat" == propertyName){
            this.passwordRepeat=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}