/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

abstract class BaseWidgetDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var type:com.gridnine.jasmine.server.standard.model.rest.WidgetTypeDT?=null

    var hSpan:Int?=null

    var notEditable:Boolean?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("type" == propertyName){
            return this.type
        }

        if("hSpan" == propertyName){
            return this.hSpan
        }

        if("notEditable" == propertyName){
            return this.notEditable
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("type" == propertyName){
            this.type=value as com.gridnine.jasmine.server.standard.model.rest.WidgetTypeDT?
            return
        }

        if("hSpan" == propertyName){
            this.hSpan=value as Int?
            return
        }

        if("notEditable" == propertyName){
            this.notEditable=value as Boolean?
            return
        }

        super.setValue(propertyName, value)
    }
}