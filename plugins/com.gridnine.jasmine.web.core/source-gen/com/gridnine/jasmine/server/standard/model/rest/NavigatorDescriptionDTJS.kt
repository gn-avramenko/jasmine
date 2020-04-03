/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class NavigatorDescriptionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDTJS(){

    var buttonsHandler:String?=null

    val viewIds = arrayListOf<String>()

    override fun getValue(propertyName: String): Any?{

        if("buttonsHandler" == propertyName){
            return this.buttonsHandler
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("buttonsHandler" == propertyName){
            this.buttonsHandler=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("viewIds" == collectionName){
            return this.viewIds as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}