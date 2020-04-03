/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EnumSelectDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDT(){

    var enumId:String?=null

    override fun getValue(propertyName: String): Any?{

        if("enumId" == propertyName){
            return this.enumId
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("enumId" == propertyName){
            this.enumId=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}