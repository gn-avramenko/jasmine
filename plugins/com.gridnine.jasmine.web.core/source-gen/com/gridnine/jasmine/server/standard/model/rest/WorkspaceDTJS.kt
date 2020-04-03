/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceDTJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    val groups = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.WorkspaceGroupDTJS>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("groups" == collectionName){
            return this.groups as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}