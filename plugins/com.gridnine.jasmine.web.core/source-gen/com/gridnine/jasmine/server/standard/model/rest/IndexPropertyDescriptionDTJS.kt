/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class IndexPropertyDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var type:com.gridnine.jasmine.server.standard.model.rest.DatabasePropertyTypeDTJS?=null

    var displayName:String?=null

    var className:String?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("type" == propertyName){
            return this.type
        }

        if("displayName" == propertyName){
            return this.displayName
        }

        if("className" == propertyName){
            return this.className
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("type" == propertyName){
            this.type=value as com.gridnine.jasmine.server.standard.model.rest.DatabasePropertyTypeDTJS?
            return
        }

        if("displayName" == propertyName){
            this.displayName=value as String?
            return
        }

        if("className" == propertyName){
            this.className=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}