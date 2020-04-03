/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class LabelDescriptionDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDTJS(){

    var displayName:String?=null

    var verticalAlignment:com.gridnine.jasmine.server.standard.model.rest.VerticalAlignmentDTJS?=null

    var horizontalAlignment:com.gridnine.jasmine.server.standard.model.rest.HorizontalAlignmentDTJS?=null

    override fun getValue(propertyName: String): Any?{

        if("displayName" == propertyName){
            return this.displayName
        }

        if("verticalAlignment" == propertyName){
            return this.verticalAlignment
        }

        if("horizontalAlignment" == propertyName){
            return this.horizontalAlignment
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("displayName" == propertyName){
            this.displayName=value as String?
            return
        }

        if("verticalAlignment" == propertyName){
            this.verticalAlignment=value as com.gridnine.jasmine.server.standard.model.rest.VerticalAlignmentDTJS?
            return
        }

        if("horizontalAlignment" == propertyName){
            this.horizontalAlignment=value as com.gridnine.jasmine.server.standard.model.rest.HorizontalAlignmentDTJS?
            return
        }

        super.setValue(propertyName, value)
    }
}