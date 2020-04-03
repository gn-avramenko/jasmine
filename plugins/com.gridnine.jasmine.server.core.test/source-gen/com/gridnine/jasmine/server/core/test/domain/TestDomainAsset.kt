/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.core.test.domain

class TestDomainAsset():com.gridnine.jasmine.server.core.model.domain.BaseAsset(){

    var stringProperty:String?=null

    var dateProperty:java.time.LocalDateTime?=null

    override fun getValue(propertyName: String): Any?{

        if("stringProperty" == propertyName){
            return this.stringProperty
        }

        if("dateProperty" == propertyName){
            return this.dateProperty
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("stringProperty" == propertyName){
            this.stringProperty=value as String?
            return
        }

        if("dateProperty" == propertyName){
            this.dateProperty=value as java.time.LocalDateTime?
            return
        }

        super.setValue(propertyName, value)
    }

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    class _TestDomainDocumentIndexProperty1(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.ComparisonSupport,com.gridnine.jasmine.server.core.storage.search.SortableProperty

    companion object{
        val stringProperty = _TestDomainDocumentIndexProperty0("stringProperty")
        val dateProperty = _TestDomainDocumentIndexProperty1("dateProperty")
    }
}