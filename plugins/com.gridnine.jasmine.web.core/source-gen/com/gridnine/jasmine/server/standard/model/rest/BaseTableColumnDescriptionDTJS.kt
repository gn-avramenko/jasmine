/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

abstract class BaseTableColumnDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var width:Int?=null

    var caption:String?=null

    var columnType:com.gridnine.jasmine.server.standard.model.rest.TableColumnTypeDTJS?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("width" == propertyName){
            return this.width
        }

        if("caption" == propertyName){
            return this.caption
        }

        if("columnType" == propertyName){
            return this.columnType
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("width" == propertyName){
            this.width=value as Int?
            return
        }

        if("caption" == propertyName){
            this.caption=value as String?
            return
        }

        if("columnType" == propertyName){
            this.columnType=value as com.gridnine.jasmine.server.standard.model.rest.TableColumnTypeDTJS?
            return
        }

        super.setValue(propertyName, value)
    }
}