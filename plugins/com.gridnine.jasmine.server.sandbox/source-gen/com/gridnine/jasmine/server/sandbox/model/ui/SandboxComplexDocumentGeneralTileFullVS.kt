/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentGeneralTileFullVS():com.gridnine.jasmine.server.core.model.ui.BaseVSEntity(){

    constructor(init: SandboxComplexDocumentGeneralTileFullVS.() ->Unit):this(){
         this.init()
    }

    var entityProperty:com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration?=null

    var enumProperty:com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum>?=null

    var entityCollection:com.gridnine.jasmine.server.core.model.ui.TableConfiguration<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVS>?=null

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
            this.entityProperty=value as com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum>?
            return
        }

        if("entityCollection" == propertyName){
            this.entityCollection=value as com.gridnine.jasmine.server.core.model.ui.TableConfiguration<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVS>?
            return
        }

        super.setValue(propertyName, value)
    }
}