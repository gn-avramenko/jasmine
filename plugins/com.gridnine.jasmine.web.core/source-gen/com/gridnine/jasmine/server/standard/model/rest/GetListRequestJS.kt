/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class GetListRequestJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var listId:String

    var sortColumn:String?=null

    var desc:Boolean?=null

    var rows:Int?=null

    var page:Int?=null

    var freeText:String?=null

    val columns = arrayListOf<String>()

    val criterions = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceCriterionDTJS>()

    val filters = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.ListFilterDTJS>()

    override fun getValue(propertyName: String): Any?{

        if("listId" == propertyName){
            return this.listId
        }

        if("sortColumn" == propertyName){
            return this.sortColumn
        }

        if("desc" == propertyName){
            return this.desc
        }

        if("rows" == propertyName){
            return this.rows
        }

        if("page" == propertyName){
            return this.page
        }

        if("freeText" == propertyName){
            return this.freeText
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("listId" == propertyName){
            this.listId=value as String
            return
        }

        if("sortColumn" == propertyName){
            this.sortColumn=value as String?
            return
        }

        if("desc" == propertyName){
            this.desc=value as Boolean?
            return
        }

        if("rows" == propertyName){
            this.rows=value as Int?
            return
        }

        if("page" == propertyName){
            this.page=value as Int?
            return
        }

        if("freeText" == propertyName){
            this.freeText=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("columns" == collectionName){
            return this.columns as MutableCollection<Any>
        }

        if("criterions" == collectionName){
            return this.criterions as MutableCollection<Any>
        }

        if("filters" == collectionName){
            return this.filters as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}