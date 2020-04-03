/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentEditorVM():com.gridnine.jasmine.server.core.model.ui.BaseVMEntity(){

    lateinit var generalTile:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVM, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVM>

    lateinit var variants:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVM, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVM>

    override fun getValue(propertyName: String): Any?{

        if("generalTile" == propertyName){
            return this.generalTile
        }

        if("variants" == propertyName){
            return this.variants
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("generalTile" == propertyName){
            this.generalTile=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVM, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVM>
            return
        }

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVM, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVM>
            return
        }

        super.setValue(propertyName, value)
    }
}