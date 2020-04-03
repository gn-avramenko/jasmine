/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentGeneralTileFullVMJS():com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS(){

    var stringProperty:String?=null

    var floatProperty:Double?=null

    var integerProperty:Int?=null

    var booleanProperty:Boolean?=null

    var entityProperty:com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS?=null

    var enumProperty:com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS?=null

    var dateProperty:kotlin.js.Date?=null

    var dateTimeProperty:kotlin.js.Date?=null

    val entityCollection = arrayListOf<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVMJS>()

    override fun getValue(propertyName: String): Any?{

        if("stringProperty" == propertyName){
            return this.stringProperty
        }

        if("floatProperty" == propertyName){
            return this.floatProperty
        }

        if("integerProperty" == propertyName){
            return this.integerProperty
        }

        if("booleanProperty" == propertyName){
            return this.booleanProperty
        }

        if("entityProperty" == propertyName){
            return this.entityProperty
        }

        if("enumProperty" == propertyName){
            return this.enumProperty
        }

        if("dateProperty" == propertyName){
            return this.dateProperty
        }

        if("dateTimeProperty" == propertyName){
            return this.dateTimeProperty
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("stringProperty" == propertyName){
            this.stringProperty=value as String?
            return
        }

        if("floatProperty" == propertyName){
            this.floatProperty=value as Double?
            return
        }

        if("integerProperty" == propertyName){
            this.integerProperty=value as Int?
            return
        }

        if("booleanProperty" == propertyName){
            this.booleanProperty=value as Boolean?
            return
        }

        if("entityProperty" == propertyName){
            this.entityProperty=value as com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS?
            return
        }

        if("dateProperty" == propertyName){
            this.dateProperty=value as kotlin.js.Date?
            return
        }

        if("dateTimeProperty" == propertyName){
            this.dateTimeProperty=value as kotlin.js.Date?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("entityCollection" == collectionName){
            return this.entityCollection as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}