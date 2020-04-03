/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileFullVV():com.gridnine.jasmine.server.core.model.ui.BaseVVEntity(){

    val variants = arrayListOf<com.gridnine.jasmine.server.core.model.ui.BaseVVEntity>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("variants" == collectionName){
            return this.variants as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}