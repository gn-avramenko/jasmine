/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

class Workspace():com.gridnine.jasmine.server.core.model.domain.BaseDocument(){

    val groups = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.WorkspaceGroup>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("groups" == collectionName){
            return this.groups as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}