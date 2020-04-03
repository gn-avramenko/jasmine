/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class AutocompleteDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var entity:String?=null

    var sortProperty:String?=null

    var sortOrder:com.gridnine.jasmine.server.standard.model.rest.AutocompleteSortOrderDT?=null

    val columns = arrayListOf<String>()

    val filters = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("entity" == propertyName){
            return this.entity
        }

        if("sortProperty" == propertyName){
            return this.sortProperty
        }

        if("sortOrder" == propertyName){
            return this.sortOrder
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("entity" == propertyName){
            this.entity=value as String?
            return
        }

        if("sortProperty" == propertyName){
            this.sortProperty=value as String?
            return
        }

        if("sortOrder" == propertyName){
            this.sortOrder=value as com.gridnine.jasmine.server.standard.model.rest.AutocompleteSortOrderDT?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("columns" == collectionName){
            return this.columns as MutableCollection<Any>
        }

        if("filters" == collectionName){
            return this.filters as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}