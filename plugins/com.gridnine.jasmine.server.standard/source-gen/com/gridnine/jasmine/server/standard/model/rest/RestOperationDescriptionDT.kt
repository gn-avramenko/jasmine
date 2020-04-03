/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class RestOperationDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var request:String?=null

    var response:String?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("request" == propertyName){
            return this.request
        }

        if("response" == propertyName){
            return this.response
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("request" == propertyName){
            this.request=value as String?
            return
        }

        if("response" == propertyName){
            this.response=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}