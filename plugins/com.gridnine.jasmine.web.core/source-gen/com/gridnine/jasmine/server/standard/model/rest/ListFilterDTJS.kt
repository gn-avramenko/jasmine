/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class ListFilterDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var fieldId:String?=null

    var value:com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS?=null

    override fun getValue(propertyName: String): Any?{

        if("fieldId" == propertyName){
            return this.fieldId
        }

        if("value" == propertyName){
            return this.value
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("fieldId" == propertyName){
            this.fieldId=value as String?
            return
        }

        if("value" == propertyName){
            this.value=value as com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS?
            return
        }

        super.setValue(propertyName, value)
    }
}