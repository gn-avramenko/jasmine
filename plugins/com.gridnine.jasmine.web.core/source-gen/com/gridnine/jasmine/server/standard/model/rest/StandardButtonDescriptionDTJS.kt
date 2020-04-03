/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class StandardButtonDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var handler:String?=null

    var displayName:String?=null

    var weight:Double?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("handler" == propertyName){
            return this.handler
        }

        if("displayName" == propertyName){
            return this.displayName
        }

        if("weight" == propertyName){
            return this.weight
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("handler" == propertyName){
            this.handler=value as String?
            return
        }

        if("displayName" == propertyName){
            this.displayName=value as String?
            return
        }

        if("weight" == propertyName){
            this.weight=value as Double?
            return
        }

        super.setValue(propertyName, value)
    }
}