/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SortOrderDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var orderType:com.gridnine.jasmine.server.standard.model.rest.SortOrderTypeDTJS?=null

    var field:String?=null

    override fun getValue(propertyName: String): Any?{

        if("orderType" == propertyName){
            return this.orderType
        }

        if("field" == propertyName){
            return this.field
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("orderType" == propertyName){
            this.orderType=value as com.gridnine.jasmine.server.standard.model.rest.SortOrderTypeDTJS?
            return
        }

        if("field" == propertyName){
            this.field=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}