/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class SandboxComplexDocumentVariantIndex():com.gridnine.jasmine.server.core.model.domain.BaseIndex<com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocument>(){

    var title:String?=null

    override fun getValue(propertyName: String): Any?{

        if("title" == propertyName){
            return this.title
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("title" == propertyName){
            this.title=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    class _TestDomainDocumentIndexProperty0(name:String):com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport(name),com.gridnine.jasmine.server.core.storage.search.EqualitySupport,com.gridnine.jasmine.server.core.storage.search.StringOperationsSupport

    companion object{
        val title = _TestDomainDocumentIndexProperty0("title")
    }
}