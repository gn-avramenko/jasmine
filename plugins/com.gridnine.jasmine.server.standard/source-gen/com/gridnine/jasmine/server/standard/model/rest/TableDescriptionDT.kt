/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class TableDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDT(){

    var className:String?=null

    val columns = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.BaseTableColumnDescriptionDT>()

    override fun getValue(propertyName: String): Any?{

        if("className" == propertyName){
            return this.className
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("className" == propertyName){
            this.className=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("columns" == collectionName){
            return this.columns as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}