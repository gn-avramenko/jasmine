/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.domain

abstract class BaseComplexWorkspaceCriterion():com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceCriterion(){

    val criterions = arrayListOf<com.gridnine.jasmine.server.sandbox.model.domain.BaseWorkspaceCriterion>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("criterions" == collectionName){
            return this.criterions as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}