/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentGeneralTileFullVSJS():com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS(){

    var entityProperty:com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS?=null

    var enumProperty:com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>?=null

    var entityCollection:com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVSJS>?=null

    override fun getValue(propertyName: String): Any?{

        if("entityProperty" == propertyName){
            return this.entityProperty
        }

        if("enumProperty" == propertyName){
            return this.enumProperty
        }

        if("entityCollection" == propertyName){
            return this.entityCollection
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("entityProperty" == propertyName){
            this.entityProperty=value as com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>?
            return
        }

        if("entityCollection" == propertyName){
            this.entityCollection=value as com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVSJS>?
            return
        }

        super.setValue(propertyName, value)
    }
}