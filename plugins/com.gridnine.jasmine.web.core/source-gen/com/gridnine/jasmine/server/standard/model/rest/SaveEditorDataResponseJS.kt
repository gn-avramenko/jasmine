/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SaveEditorDataResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    var viewModel:com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS?=null

    var viewSettings:com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS?=null

    var viewValidation:com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS?=null

    var title:String?=null

    override fun getValue(propertyName: String): Any?{

        if("viewModel" == propertyName){
            return this.viewModel
        }

        if("viewSettings" == propertyName){
            return this.viewSettings
        }

        if("viewValidation" == propertyName){
            return this.viewValidation
        }

        if("title" == propertyName){
            return this.title
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("viewModel" == propertyName){
            this.viewModel=value as com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS?
            return
        }

        if("viewSettings" == propertyName){
            this.viewSettings=value as com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS?
            return
        }

        if("viewValidation" == propertyName){
            this.viewValidation=value as com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS?
            return
        }

        if("title" == propertyName){
            this.title=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}