/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class RestEntityDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var abstract:Boolean?=null

    var extends:String?=null

    val properties = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.RestPropertyDescriptionDTJS>()

    val collections = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.RestCollectionDescriptionDTJS>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("abstract" == propertyName){
            return this.abstract
        }

        if("extends" == propertyName){
            return this.extends
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("abstract" == propertyName){
            this.abstract=value as Boolean?
            return
        }

        if("extends" == propertyName){
            this.extends=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("properties" == collectionName){
            return this.properties as MutableCollection<Any>
        }

        if("collections" == collectionName){
            return this.collections as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}