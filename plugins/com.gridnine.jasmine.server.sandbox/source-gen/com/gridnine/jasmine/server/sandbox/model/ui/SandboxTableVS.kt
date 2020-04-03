/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxTableVS():com.gridnine.jasmine.server.core.model.ui.BaseVSEntity(){

    constructor(init: SandboxTableVS.() ->Unit):this(){
         this.init()
    }

    var entityColumn:com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration?=null

    var enumColumn:com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum>?=null

    override fun getValue(propertyName: String): Any?{

        if("entityColumn" == propertyName){
            return this.entityColumn
        }

        if("enumColumn" == propertyName){
            return this.enumColumn
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("entityColumn" == propertyName){
            this.entityColumn=value as com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration?
            return
        }

        if("enumColumn" == propertyName){
            this.enumColumn=value as com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum>?
            return
        }

        super.setValue(propertyName, value)
    }
}