/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class VMPropertyDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var type:com.gridnine.jasmine.server.standard.model.rest.VMPropertyTypeDT?=null

    var className:String?=null

    var nonNullable:Boolean?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("type" == propertyName){
            return this.type
        }

        if("className" == propertyName){
            return this.className
        }

        if("nonNullable" == propertyName){
            return this.nonNullable
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("type" == propertyName){
            this.type=value as com.gridnine.jasmine.server.standard.model.rest.VMPropertyTypeDT?
            return
        }

        if("className" == propertyName){
            this.className=value as String?
            return
        }

        if("nonNullable" == propertyName){
            this.nonNullable=value as Boolean?
            return
        }

        super.setValue(propertyName, value)
    }
}