/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.core.test.domain

class TestDomainDocument():com.gridnine.jasmine.server.core.model.domain.BaseDocument(){

    var stringProperty:String?=null

    var entityProperty:com.gridnine.jasmine.server.core.test.domain.BaseTestDomainNestedDocument?=null

    var enumProperty:com.gridnine.jasmine.server.core.test.domain.TestEnum?=null

    val stringCollection = arrayListOf<String>()

    val entityCollection = arrayListOf<com.gridnine.jasmine.server.core.test.domain.BaseTestDomainNestedDocument>()

    val groups = arrayListOf<com.gridnine.jasmine.server.core.test.domain.TestGroup>()

    override fun getValue(propertyName: String): Any?{

        if("stringProperty" == propertyName){
            return this.stringProperty
        }

        if("entityProperty" == propertyName){
            return this.entityProperty
        }

        if("enumProperty" == propertyName){
            return this.enumProperty
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("stringProperty" == propertyName){
            this.stringProperty=value as String?
            return
        }

        if("entityProperty" == propertyName){
            this.entityProperty=value as com.gridnine.jasmine.server.core.test.domain.BaseTestDomainNestedDocument?
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.server.core.test.domain.TestEnum?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("stringCollection" == collectionName){
            return this.stringCollection as MutableCollection<Any>
        }

        if("entityCollection" == collectionName){
            return this.entityCollection as MutableCollection<Any>
        }

        if("groups" == collectionName){
            return this.groups as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}