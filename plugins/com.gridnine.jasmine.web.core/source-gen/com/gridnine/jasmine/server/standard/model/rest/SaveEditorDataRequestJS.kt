/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SaveEditorDataRequestJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var objectId:String

    lateinit var viewModel:com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS

    override fun getValue(propertyName: String): Any?{

        if("objectId" == propertyName){
            return this.objectId
        }

        if("viewModel" == propertyName){
            return this.viewModel
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("objectId" == propertyName){
            this.objectId=value as String
            return
        }

        if("viewModel" == propertyName){
            this.viewModel=value as com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS
            return
        }

        super.setValue(propertyName, value)
    }
}