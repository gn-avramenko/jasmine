/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileFullVVJS():com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS(){

    val variants = arrayListOf<com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("variants" == collectionName){
            return this.variants as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}