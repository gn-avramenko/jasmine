/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class StandardViewDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseViewDescriptionDT(){

    var layout:com.gridnine.jasmine.server.standard.model.rest.BaseLayoutDescriptionDT?=null

    override fun getValue(propertyName: String): Any?{

        if("layout" == propertyName){
            return this.layout
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("layout" == propertyName){
            this.layout=value as com.gridnine.jasmine.server.standard.model.rest.BaseLayoutDescriptionDT?
            return
        }

        super.setValue(propertyName, value)
    }
}