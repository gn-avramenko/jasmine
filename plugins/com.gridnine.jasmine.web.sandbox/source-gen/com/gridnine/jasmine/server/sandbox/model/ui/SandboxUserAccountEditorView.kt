/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxUserAccountEditorView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxUserAccountEditorVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxUserAccountEditorVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxUserAccountEditorVVJS>(){

    lateinit var name:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var login:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

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
            this.name=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        if("login" == propertyName){
            this.login=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        super.setValue(propertyName, value)
    }
}