/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class GetEditorDataResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var viewModel:com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS

    lateinit var viewSettings:com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS

    lateinit var title:String

    override fun getValue(propertyName: String): Any?{

        if("viewModel" == propertyName){
            return this.viewModel
        }

        if("viewSettings" == propertyName){
            return this.viewSettings
        }

        if("title" == propertyName){
            return this.title
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("viewModel" == propertyName){
            this.viewModel=value as com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS
            return
        }

        if("viewSettings" == propertyName){
            this.viewSettings=value as com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS
            return
        }

        if("title" == propertyName){
            this.title=value as String
            return
        }

        super.setValue(propertyName, value)
    }
}