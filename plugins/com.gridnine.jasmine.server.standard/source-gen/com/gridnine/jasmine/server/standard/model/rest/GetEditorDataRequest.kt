/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class GetEditorDataRequest():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    lateinit var objectId:String

    var objectUid:String?=null

    override fun getValue(propertyName: String): Any?{

        if("objectId" == propertyName){
            return this.objectId
        }

        if("objectUid" == propertyName){
            return this.objectUid
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("objectId" == propertyName){
            this.objectId=value as String
            return
        }

        if("objectUid" == propertyName){
            this.objectUid=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}