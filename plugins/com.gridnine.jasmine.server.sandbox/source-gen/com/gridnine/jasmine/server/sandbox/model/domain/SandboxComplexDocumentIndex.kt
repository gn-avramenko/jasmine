/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxComplexDocumentIndex():com.gridnine.jasmine.server.core.model.domain.BaseIndex<com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocument>(){

    var stringProperty:String?=null

    var floatProperty:java.math.BigDecimal?=null

    var integerProperty:Int?=null

    var booleanProperty:Boolean?=null

    var entityRefProperty:com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>?=null

    var enumProperty:com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnum?=null

    var dateProperty:java.time.LocalDate?=null

    var dateTimeProperty:java.time.LocalDateTime?=null

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
            this.integerProperty=value as Int?
            return
        }

        if("booleanProperty" == propertyName){
            this.booleanProperty=value as Boolean?
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

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    class _TestDomainDocumentIndexProperty1(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.ComparisonSupport,com.gridnine.jasmine.server.core.storage.search.NumberOperationsSupport,com.gridnine.jasmine.server.core.storage.search.SortableProperty

    class _TestDomainDocumentIndexProperty2(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.ComparisonSupport,com.gridnine.jasmine.server.core.storage.search.NumberOperationsSupport,com.gridnine.jasmine.server.core.storage.search.SortableProperty

    class _TestDomainDocumentIndexProperty3(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name)

    class _TestDomainDocumentIndexProperty4(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    class _TestDomainDocumentIndexProperty5(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport

    class _TestDomainDocumentIndexProperty6(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.ComparisonSupport,com.gridnine.jasmine.server.core.storage.search.SortableProperty

    class _TestDomainDocumentIndexProperty7(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.ComparisonSupport,com.gridnine.jasmine.server.core.storage.search.SortableProperty

    companion object{
        val stringProperty = _TestDomainDocumentIndexProperty0("stringProperty")
        val floatProperty = _TestDomainDocumentIndexProperty1("floatProperty")
        val integerProperty = _TestDomainDocumentIndexProperty2("integerProperty")
        val booleanProperty = _TestDomainDocumentIndexProperty3("booleanProperty")
        val entityRefProperty = _TestDomainDocumentIndexProperty4("entityRefProperty")
        val enumProperty = _TestDomainDocumentIndexProperty5("enumProperty")
        val dateProperty = _TestDomainDocumentIndexProperty6("dateProperty")
        val dateTimeProperty = _TestDomainDocumentIndexProperty7("dateTimeProperty")
    }
}