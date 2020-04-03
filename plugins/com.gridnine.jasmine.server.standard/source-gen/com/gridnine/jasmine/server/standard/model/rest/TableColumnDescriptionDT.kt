/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class TableColumnDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var width:String?=null

    override fun getValue(propertyName: String): Any?{

        if("width" == propertyName){
            return this.width
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("width" == propertyName){
            this.width=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}