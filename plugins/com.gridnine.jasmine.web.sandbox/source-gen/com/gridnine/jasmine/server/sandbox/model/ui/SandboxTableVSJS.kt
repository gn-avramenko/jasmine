/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxTableVSJS():com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS(){

    var entityColumn:com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS?=null

    var enumColumn:com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>?=null

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
            this.entityColumn=value as com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS?
            return
        }

        if("enumColumn" == propertyName){
            this.enumColumn=value as com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>?
            return
        }

        super.setValue(propertyName, value)
    }
}