/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentEditorVVJS():com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS(){

    var generalTile:com.gridnine.jasmine.web.core.model.ui.TileDataJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVVJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVVJS>?=null

    var variants:com.gridnine.jasmine.web.core.model.ui.TileDataJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVVJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVVJS>?=null

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
            this.generalTile=value as com.gridnine.jasmine.web.core.model.ui.TileDataJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactVVJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVVJS>?
            return
        }

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.web.core.model.ui.TileDataJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVVJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVVJS>?
            return
        }

        super.setValue(propertyName, value)
    }
}