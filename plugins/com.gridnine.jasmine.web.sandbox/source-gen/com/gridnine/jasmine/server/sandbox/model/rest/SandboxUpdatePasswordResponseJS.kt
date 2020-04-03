/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxUpdatePasswordResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var validation:com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVVJS

    override fun getValue(propertyName: String): Any?{

        if("validation" == propertyName){
            return this.validation
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("validation" == propertyName){
            this.validation=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxUpdatePasswordDialogVVJS
            return
        }

        super.setValue(propertyName, value)
    }
}