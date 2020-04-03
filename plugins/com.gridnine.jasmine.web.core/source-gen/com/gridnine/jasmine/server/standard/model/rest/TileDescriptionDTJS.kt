/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class TileDescriptionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDTJS(){

    var compactViewId:String?=null

    var fullViewId:String?=null

    var displayName:String?=null

    override fun getValue(propertyName: String): Any?{

        if("compactViewId" == propertyName){
            return this.compactViewId
        }

        if("fullViewId" == propertyName){
            return this.fullViewId
        }

        if("displayName" == propertyName){
            return this.displayName
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("compactViewId" == propertyName){
            this.compactViewId=value as String?
            return
        }

        if("fullViewId" == propertyName){
            this.fullViewId=value as String?
            return
        }

        if("displayName" == propertyName){
            this.displayName=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}