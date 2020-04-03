/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class IndexCollectionDescriptionDT():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var id:String?=null

    var elementType:com.gridnine.jasmine.server.standard.model.rest.DatabaseCollectionTypeDT?=null

    var displayName:String?=null

    var elementClassName:String?=null

    override fun getValue(propertyName: String): Any?{

        if("id" == propertyName){
            return this.id
        }

        if("elementType" == propertyName){
            return this.elementType
        }

        if("displayName" == propertyName){
            return this.displayName
        }

        if("elementClassName" == propertyName){
            return this.elementClassName
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("id" == propertyName){
            this.id=value as String?
            return
        }

        if("elementType" == propertyName){
            this.elementType=value as com.gridnine.jasmine.server.standard.model.rest.DatabaseCollectionTypeDT?
            return
        }

        if("displayName" == propertyName){
            this.displayName=value as String?
            return
        }

        if("elementClassName" == propertyName){
            this.elementClassName=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}