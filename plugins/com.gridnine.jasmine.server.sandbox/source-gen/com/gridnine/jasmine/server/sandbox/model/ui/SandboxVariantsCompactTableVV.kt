/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxVariantsCompactTableVV():com.gridnine.jasmine.server.core.model.ui.BaseVVEntity(){

    var name:String?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("name" == propertyName){
            this.name=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}