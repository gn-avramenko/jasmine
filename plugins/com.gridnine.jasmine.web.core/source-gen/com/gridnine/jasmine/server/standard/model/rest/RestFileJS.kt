/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class RestFileJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var name:String?=null

    var type:String?=null

    var content:String?=null

    override fun getValue(propertyName: String): Any?{

        if("name" == propertyName){
            return this.name
        }

        if("type" == propertyName){
            return this.type
        }

        if("content" == propertyName){
            return this.content
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("name" == propertyName){
            this.name=value as String?
            return
        }

        if("type" == propertyName){
            this.type=value as String?
            return
        }

        if("content" == propertyName){
            this.content=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}