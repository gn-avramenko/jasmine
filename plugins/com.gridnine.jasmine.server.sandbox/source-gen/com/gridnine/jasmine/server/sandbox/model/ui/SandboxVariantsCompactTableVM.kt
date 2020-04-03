/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxVariantsCompactTableVM():com.gridnine.jasmine.server.core.model.ui.BaseVMEntity(){

    var name:String?=null

    var navigation:com.gridnine.jasmine.server.core.model.ui.NavigationTableColumnData?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        if("navigation" == propertyName){
            return this.navigation
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("name" == propertyName){
            this.name=value as String?
            return
        }

        if("navigation" == propertyName){
            this.navigation=value as com.gridnine.jasmine.server.core.model.ui.NavigationTableColumnData?
            return
        }

        super.setValue(propertyName, value)
    }
}