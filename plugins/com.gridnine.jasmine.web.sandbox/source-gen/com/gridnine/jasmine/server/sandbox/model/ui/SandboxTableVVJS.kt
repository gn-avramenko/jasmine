/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxTableVVJS():com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS(){

    var textColumn:String?=null

    var entityColumn:String?=null

    var enumColumn:String?=null

    var floatColumn:String?=null

    var integerColumn:String?=null

    override fun getValue(propertyName: String): Any?{

        if("textColumn" == propertyName){
            return this.textColumn
        }

        if("entityColumn" == propertyName){
            return this.entityColumn
        }

        if("enumColumn" == propertyName){
            return this.enumColumn
        }

        if("floatColumn" == propertyName){
            return this.floatColumn
        }

        if("integerColumn" == propertyName){
            return this.integerColumn
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("textColumn" == propertyName){
            this.textColumn=value as String?
            return
        }

        if("entityColumn" == propertyName){
            this.entityColumn=value as String?
            return
        }

        if("enumColumn" == propertyName){
            this.enumColumn=value as String?
            return
        }

        if("floatColumn" == propertyName){
            this.floatColumn=value as String?
            return
        }

        if("integerColumn" == propertyName){
            this.integerColumn=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}