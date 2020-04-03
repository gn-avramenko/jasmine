/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

abstract class BaseViewDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var type:com.gridnine.jasmine.server.standard.model.rest.ViewTypeDTJS?=null

    var viewModel:String?=null

    var viewSettings:String?=null

    var viewValidation:String?=null

    var viewHandler:String?=null

    val interceptors = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("type" == propertyName){
            return this.type
        }

        if("viewModel" == propertyName){
            return this.viewModel
        }

        if("viewSettings" == propertyName){
            return this.viewSettings
        }

        if("viewValidation" == propertyName){
            return this.viewValidation
        }

        if("viewHandler" == propertyName){
            return this.viewHandler
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("type" == propertyName){
            this.type=value as com.gridnine.jasmine.server.standard.model.rest.ViewTypeDTJS?
            return
        }

        if("viewModel" == propertyName){
            this.viewModel=value as String?
            return
        }

        if("viewSettings" == propertyName){
            this.viewSettings=value as String?
            return
        }

        if("viewValidation" == propertyName){
            this.viewValidation=value as String?
            return
        }

        if("viewHandler" == propertyName){
            this.viewHandler=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("interceptors" == collectionName){
            return this.interceptors as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}