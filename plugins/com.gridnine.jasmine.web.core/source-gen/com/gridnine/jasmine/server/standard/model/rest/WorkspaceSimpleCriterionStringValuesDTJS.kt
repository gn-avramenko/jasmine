/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class WorkspaceSimpleCriterionStringValuesDTJS():com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS(){

    val values = arrayListOf<String>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("values" == collectionName){
            return this.values as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}