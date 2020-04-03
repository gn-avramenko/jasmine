/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class LabelDescriptionDT():com.gridnine.jasmine.server.standard.model.rest.BaseWidgetDescriptionDT(){

    var displayName:String?=null

    var verticalAlignment:com.gridnine.jasmine.server.standard.model.rest.VerticalAlignmentDT?=null

    var horizontalAlignment:com.gridnine.jasmine.server.standard.model.rest.HorizontalAlignmentDT?=null

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
            this.verticalAlignment=value as com.gridnine.jasmine.server.standard.model.rest.VerticalAlignmentDT?
            return
        }

        if("horizontalAlignment" == propertyName){
            this.horizontalAlignment=value as com.gridnine.jasmine.server.standard.model.rest.HorizontalAlignmentDT?
            return
        }

        super.setValue(propertyName, value)
    }
}