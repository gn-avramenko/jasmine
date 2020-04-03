/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxUserAccountIndex():com.gridnine.jasmine.server.core.model.domain.BaseIndex<com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount>(){

    var name:String?=null

    var login:String?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        if("login" == propertyName){
            return this.login
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("name" == propertyName){
            this.name=value as String?
            return
        }

        if("login" == propertyName){
            this.login=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    class _TestDomainDocumentIndexProperty1(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    companion object{
        val name = _TestDomainDocumentIndexProperty0("name")
        val login = _TestDomainDocumentIndexProperty1("login")
    }
}