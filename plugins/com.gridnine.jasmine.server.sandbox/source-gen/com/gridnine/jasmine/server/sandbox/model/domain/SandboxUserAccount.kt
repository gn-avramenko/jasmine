/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxUserAccount():com.gridnine.jasmine.server.core.model.domain.BaseDocument(){

    var name:String?=null

    var login:String?=null

    var password:String?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        if("login" == propertyName){
            return this.login
        }

        if("password" == propertyName){
            return this.password
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

        if("password" == propertyName){
            this.password=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    
            override fun toString():String = name?:"???"
        
}