/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class SaveWorkspaceResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    lateinit var workspace:com.gridnine.jasmine.server.standard.model.rest.WorkspaceDTJS

    override fun getValue(propertyName: String): Any?{

        if("workspace" == propertyName){
            return this.workspace
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("workspace" == propertyName){
            this.workspace=value as com.gridnine.jasmine.server.standard.model.rest.WorkspaceDTJS
            return
        }

        super.setValue(propertyName, value)
    }
}