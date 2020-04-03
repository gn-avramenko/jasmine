/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

abstract class BaseLayoutDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var type:com.gridnine.jasmine.server.standard.model.rest.LayoutTypeDT?=null

    val widgets = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDT>()

    override fun getValue(propertyName: String): Any?{

        if("type" == propertyName){
            return this.type
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("type" == propertyName){
            this.type=value as com.gridnine.jasmine.server.standard.model.rest.LayoutTypeDT?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("widgets" == collectionName){
            return this.widgets as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}