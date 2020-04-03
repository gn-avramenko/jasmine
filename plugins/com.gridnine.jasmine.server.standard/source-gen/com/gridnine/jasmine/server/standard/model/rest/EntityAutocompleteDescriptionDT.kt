/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EntityAutocompleteDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDT(){

    var entityClassName:String?=null

    override fun getValue(propertyName: String): Any?{

        if("entityClassName" == propertyName){
            return this.entityClassName
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("entityClassName" == propertyName){
            this.entityClassName=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}