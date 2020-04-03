/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class LoginResponse():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var validation:com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVV?=null

    var successfull:Boolean?=null

    override fun getValue(propertyName: String): Any?{

        if("validation" == propertyName){
            return this.validation
        }

        if("successfull" == propertyName){
            return this.successfull
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("validation" == propertyName){
            this.validation=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVV?
            return
        }

        if("successfull" == propertyName){
            this.successfull=value as Boolean?
            return
        }

        super.setValue(propertyName, value)
    }
}