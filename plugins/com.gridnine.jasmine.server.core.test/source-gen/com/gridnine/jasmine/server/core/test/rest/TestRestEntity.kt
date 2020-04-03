/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.core.test.rest

class TestRestEntity():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var stringField:String?=null

    var enumField:com.gridnine.jasmine.server.core.test.rest.TestRestEnum?=null

    override fun getValue(propertyName: String): Any?{

        if("stringField" == propertyName){
            return this.stringField
        }

        if("enumField" == propertyName){
            return this.enumField
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("stringField" == propertyName){
            this.stringField=value as String?
            return
        }

        if("enumField" == propertyName){
            this.enumField=value as com.gridnine.jasmine.server.core.test.rest.TestRestEnum?
            return
        }

        super.setValue(propertyName, value)
    }
}