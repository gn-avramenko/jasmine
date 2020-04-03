/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxUserAccountEditorVM():com.gridnine.jasmine.server.core.model.ui.BaseVMEntity(){

    var name:String?=null

    var login:String?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        if("login" == propertyName){
            return this.login
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("name" == propertyName){
            this.name=value as String?
            return
        }

        if("login" == propertyName){
            this.login=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}