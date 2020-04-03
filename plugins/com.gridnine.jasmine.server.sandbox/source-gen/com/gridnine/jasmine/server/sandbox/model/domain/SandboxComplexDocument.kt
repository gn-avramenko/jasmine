/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxComplexDocument():com.gridnine.jasmine.server.core.model.domain.BaseDocument(){

    var stringProperty:String?=null

    var floatProperty:java.math.BigDecimal?=null

    var integerProperty:Int=0

    var booleanProperty:Boolean=false

    var entityRefProperty:com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?=null

    var enumProperty:com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?=null

    var dateProperty:java.time.LocalDate?=null

    var dateTimeProperty:java.time.LocalDateTime?=null

    val entityCollection = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.SandboxNestedDocument>()

    val nestedDocuments = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.BaseSandboxNavigatorVariant>()

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

        if("entityRefProperty" == propertyName){
            return this.entityRefProperty
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
            this.floatProperty=value as java.math.BigDecimal?
            return
        }

        if("integerProperty" == propertyName){
            this.integerProperty=value as Int
            return
        }

        if("booleanProperty" == propertyName){
            this.booleanProperty=value as Boolean
            return
        }

        if("entityRefProperty" == propertyName){
            this.entityRefProperty=value as com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?
            return
        }

        if("dateProperty" == propertyName){
            this.dateProperty=value as java.time.LocalDate?
            return
        }

        if("dateTimeProperty" == propertyName){
            this.dateTimeProperty=value as java.time.LocalDateTime?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("entityCollection" == collectionName){
            return this.entityCollection as MutableCollection<Any>
        }

        if("nestedDocuments" == collectionName){
            return this.nestedDocuments as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}