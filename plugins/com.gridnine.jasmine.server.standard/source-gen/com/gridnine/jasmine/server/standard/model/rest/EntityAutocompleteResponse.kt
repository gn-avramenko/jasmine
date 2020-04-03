/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class EntityAutocompleteResponse():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    val items = arrayListOf<com.gridnine.jasmine.server.core.model.domain.EntityReference<com.gridnine.jasmine.server.core.model.common.BaseEntity>>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("items" == collectionName){
            return this.items as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}