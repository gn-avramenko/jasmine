/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class SandboxCreateUserAccountResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var validation:com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVVJS

    var result:com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS?=null

    override fun getValue(propertyName: String): Any?{

        if("validation" == propertyName){
            return this.validation
        }

        if("result" == propertyName){
            return this.result
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("validation" == propertyName){
            this.validation=value as com.gridnine.jasmine.server.sandbox.model.ui.SandboxCreateUserAccountDialogVVJS
            return
        }

        if("result" == propertyName){
            this.result=value as com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS?
            return
        }

        super.setValue(propertyName, value)
    }
}