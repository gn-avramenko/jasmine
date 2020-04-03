/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxVariantsCompactTableVMJS():com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS(){

    var name:String?=null

    var navigation:com.gridnine.jasmine.web.core.model.ui.NavigationTableColumnDataJS?=null

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
            this.navigation=value as com.gridnine.jasmine.web.core.model.ui.NavigationTableColumnDataJS?
            return
        }

        super.setValue(propertyName, value)
    }
}