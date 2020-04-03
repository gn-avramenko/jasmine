/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentEditorVV():com.gridnine.jasmine.server.core.model.ui.BaseVVEntity(){

    lateinit var generalTile:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVV, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVV>

    lateinit var variants:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVV, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVV>

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
            this.generalTile=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVV, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVV>
            return
        }

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVV, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVV>
            return
        }

        super.setValue(propertyName, value)
    }
}