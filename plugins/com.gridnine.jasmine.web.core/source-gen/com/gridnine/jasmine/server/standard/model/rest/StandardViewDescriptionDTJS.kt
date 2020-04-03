/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class StandardViewDescriptionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseViewDescriptionDTJS(){

    var layout:com.gridnine.jasmine.server.standard.model.rest.BaseLayoutDescriptionDTJS?=null

    override fun getValue(propertyName: String): Any?{

        if("layout" == propertyName){
            return this.layout
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("layout" == propertyName){
            this.layout=value as com.gridnine.jasmine.server.standard.model.rest.BaseLayoutDescriptionDTJS?
            return
        }

        super.setValue(propertyName, value)
    }
}