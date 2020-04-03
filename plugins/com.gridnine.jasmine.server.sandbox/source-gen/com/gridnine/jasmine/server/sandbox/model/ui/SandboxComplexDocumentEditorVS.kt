/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentEditorVS():com.gridnine.jasmine.server.core.model.ui.BaseVSEntity(){

    constructor(init: SandboxComplexDocumentEditorVS.() ->Unit):this(){
         this.init()
    }

    lateinit var generalTile:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVS, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVS>

    lateinit var variants:com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVS, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVS>

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
            this.generalTile=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVS, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVS>
            return
        }

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.server.core.model.ui.TileData<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVS, com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVS>
            return
        }

        super.setValue(propertyName, value)
    }
}