/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxLoginDialogVVJS():com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS(){

    var login:String?=null

    var password:String?=null

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
            this.login=value as String?
            return
        }

        if("password" == propertyName){
            this.password=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}