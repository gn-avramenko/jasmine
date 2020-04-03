/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class BooleanBoxDescriptionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDTJS(){

    var nonNullable:Boolean?=null

    override fun getValue(propertyName: String): Any?{

        if("nonNullable" == propertyName){
            return this.nonNullable
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("nonNullable" == propertyName){
            this.nonNullable=value as Boolean?
            return
        }

        super.setValue(propertyName, value)
    }
}