/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SaveEditorDataResponse():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var viewModel:com.gridnine.jasmine.server.core.model.ui.BaseVMEntity?=null

    var viewSettings:com.gridnine.jasmine.server.core.model.ui.BaseVSEntity?=null

    var viewValidation:com.gridnine.jasmine.server.core.model.ui.BaseVVEntity?=null

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
            this.viewModel=value as com.gridnine.jasmine.server.core.model.ui.BaseVMEntity?
            return
        }

        if("viewSettings" == propertyName){
            this.viewSettings=value as com.gridnine.jasmine.server.core.model.ui.BaseVSEntity?
            return
        }

        if("viewValidation" == propertyName){
            this.viewValidation=value as com.gridnine.jasmine.server.core.model.ui.BaseVVEntity?
            return
        }

        if("title" == propertyName){
            this.title=value as String?
            return
        }

        super.setValue(propertyName, value)
    }
}