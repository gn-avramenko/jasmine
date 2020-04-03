/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class DialogDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var viewId:String?=null

    var closable:Boolean?=null

    var title:String?=null

    val buttons = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.StandardButtonDescriptionDT>()

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("viewId" == propertyName){
            return this.viewId
        }

        if("closable" == propertyName){
            return this.closable
        }

        if("title" == propertyName){
            return this.title
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("viewId" == propertyName){
            this.viewId=value as String?
            return
        }

        if("closable" == propertyName){
            this.closable=value as Boolean?
            return
        }

        if("title" == propertyName){
            this.title=value as String?
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