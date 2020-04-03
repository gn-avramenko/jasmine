/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class TableLayoutDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseLayoutDescriptionDT(){

    var expandLastRow:Boolean?=null

    val columns = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.TableColumnDescriptionDT>()

    override fun getValue(propertyName: String): Any?{

        if("expandLastRow" == propertyName){
            return this.expandLastRow
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("expandLastRow" == propertyName){
            this.expandLastRow=value as Boolean?
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