/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentEditorView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentEditorVVJS>(){

    lateinit var generalTile:com.gridnine.jasmine.web.core.model.ui.TileWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactView,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullView>

    lateinit var variants:com.gridnine.jasmine.web.core.model.ui.TileWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactView,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullView>

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
            this.generalTile=value as com.gridnine.jasmine.web.core.model.ui.TileWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileCompactView,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullView>
            return
        }

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.web.core.model.ui.TileWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactView,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullView>
            return
        }

        super.setValue(propertyName, value)
    }
}