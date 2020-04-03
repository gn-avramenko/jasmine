/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxNavigatorVariant1():com.gridnine.jasmine.server.sandbox.model.domain.BaseSandboxNavigatorVariant(){

    var intValue:Int=0

    override fun getValue(propertyName: String): Any?{

        if("intValue" == propertyName){
            return this.intValue
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("intValue" == propertyName){
            this.intValue=value as Int
            return
        }

        super.setValue(propertyName, value)
    }
}