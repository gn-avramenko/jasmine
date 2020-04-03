/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EditorDescriptionDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var id:String?=null

    var entityId:String?=null

    var viewId:String?=null

    val buttons = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.StandardButtonDescriptionDTJS>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("entityId" == propertyName){
            return this.entityId
        }

        if("viewId" == propertyName){
            return this.viewId
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("entityId" == propertyName){
            this.entityId=value as String?
            return
        }

        if("viewId" == propertyName){
            this.viewId=value as String?
            return
        }

        super.setValue(propertyName, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("buttons" == collectionName){
            return this.buttons as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}