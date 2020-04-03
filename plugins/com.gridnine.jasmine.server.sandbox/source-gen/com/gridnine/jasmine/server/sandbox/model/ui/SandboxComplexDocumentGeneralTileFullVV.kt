/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentGeneralTileFullVV():com.gridnine.jasmine.server.core.model.ui.BaseVVEntity(){

    var stringProperty:String?=null

    var floatProperty:String?=null

    var integerProperty:String?=null

    var booleanProperty:String?=null

    var entityProperty:String?=null

    var enumProperty:String?=null

    var dateProperty:String?=null

    var dateTimeProperty:String?=null

    val entityCollection = arrayListOf<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVV>()

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
            this.floatProperty=value as String?
            return
        }

        if("integerProperty" == propertyName){
            this.integerProperty=value as String?
            return
        }

        if("booleanProperty" == propertyName){
            this.booleanProperty=value as String?
            return
        }

        if("entityProperty" == propertyName){
            this.entityProperty=value as String?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as String?
            return
        }

        if("dateProperty" == propertyName){
            this.dateProperty=value as String?
            return
        }

        if("dateTimeProperty" == propertyName){
            this.dateTimeProperty=value as String?
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