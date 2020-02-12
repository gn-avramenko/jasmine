/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/


package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.model.ui.EntityAutocompleteConfiguration
import com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration
import com.gridnine.jasmine.server.core.model.ui.TableConfiguration
import com.gridnine.jasmine.server.core.model.ui.TileData
import com.gridnine.jasmine.server.core.utils.UiUtils
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxComplexDocument
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxNestedDocument
import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.server.standard.rest.RestEditorHandler


class SandboxComplexDocumentEditorHandler : RestEditorHandler<SandboxComplexDocument, SandboxComplexDocumentEditorVM, SandboxComplexDocumentEditorVS, SandboxComplexDocumentEditorVV>{

    override fun read(entity: SandboxComplexDocument?, vmEntity: SandboxComplexDocumentEditorVM, ctx: MutableMap<String, Any>) {
        vmEntity.generalTile = TileData()
        vmEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVM()
        vmEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVM()
        if(entity == null){
            vmEntity.generalTile .compactData.stringProperty = "Новый объект"
            return
        }
        vmEntity.generalTile.compactData.stringProperty = entity.stringProperty
        vmEntity.generalTile.fullData.stringProperty = entity.stringProperty
        vmEntity.generalTile.fullData.booleanProperty = entity.booleanProperty
        vmEntity.generalTile.fullData.dateProperty = entity.dateProperty
        vmEntity.generalTile.fullData.dateTimeProperty = entity.dateTimeProperty
        vmEntity.generalTile.fullData.entityProperty = entity.entityRefProperty
        vmEntity.generalTile.fullData.enumProperty = entity.enumProperty
        vmEntity.generalTile.fullData.floatProperty = entity.floatProperty
        vmEntity.generalTile.fullData.integerProperty = entity.integerProperty
        entity.entityCollection.forEach {
            val item = SandboxTableVM()
            item.uid = it.uid
            vmEntity.generalTile.fullData.entityCollection.add(item)
            item.floatColumn = it.floatColumn
            item.integerColumn = it.integerColumn
            item.textColumn = it.textColumn
        }
    }

    override fun write(entity: SandboxComplexDocument, vmEntity: SandboxComplexDocumentEditorVM, ctx: MutableMap<String, Any>) {
        entity.stringProperty = vmEntity.generalTile.fullData.stringProperty
        entity.booleanProperty = vmEntity.generalTile.fullData.booleanProperty
        entity.dateProperty = vmEntity.generalTile.fullData.dateProperty
        entity.dateTimeProperty = vmEntity.generalTile.fullData.dateTimeProperty
        entity.integerProperty = vmEntity.generalTile.fullData.integerProperty
        entity.floatProperty = vmEntity.generalTile.fullData.floatProperty
        entity.enumProperty = vmEntity.generalTile.fullData.enumProperty
        UiUtils.writeCollection(vmEntity.generalTile.fullData.entityCollection, entity.entityCollection, SandboxNestedDocument::class){ sandboxTableVM, sandboxNestedDocument ->
            sandboxNestedDocument.integerColumn = sandboxTableVM.integerColumn
            sandboxNestedDocument.floatColumn = sandboxTableVM.floatColumn
            sandboxNestedDocument.textColumn = sandboxTableVM.textColumn
        }
    }

    override fun getTitle(entity: SandboxComplexDocument?, vmEntity: SandboxComplexDocumentEditorVM, vsEntity: SandboxComplexDocumentEditorVS, ctx: MutableMap<String, Any>): String? {
        return entity?.stringProperty?:"Новый документ"
    }

    override fun fillSettings(entity: SandboxComplexDocument?, vsEntity: SandboxComplexDocumentEditorVS, vmEntity: SandboxComplexDocumentEditorVM, ctx: MutableMap<String, Any>) {
        vsEntity.generalTile = TileData()
        vsEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVS{
            entityCollection = TableConfiguration{
                columnSettings = SandboxTableVS()
            }
            entityProperty = EntityAutocompleteConfiguration()
            enumProperty = EnumSelectConfiguration()
        }
        vsEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVS()
    }

    override fun validate(vmEntity: SandboxComplexDocumentEditorVM, vvEntity: SandboxComplexDocumentEditorVV, ctx: MutableMap<String, Any>) {
       vvEntity.generalTile = TileData()
       vvEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVV()
        vvEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVV()
    }

}


