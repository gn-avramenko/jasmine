/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/


package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.model.ui.EnumSelectConfiguration
import com.gridnine.jasmine.server.core.model.ui.NavigationTableColumnData
import com.gridnine.jasmine.server.core.model.ui.TableConfiguration
import com.gridnine.jasmine.server.core.model.ui.TileData
import com.gridnine.jasmine.server.core.utils.UiUtils
import com.gridnine.jasmine.server.sandbox.model.domain.*
import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.server.standard.model.ui.StandardValidationMessages
import com.gridnine.jasmine.server.standard.rest.RestEditorHandler


class SandboxComplexDocumentEditorHandler : RestEditorHandler<SandboxComplexDocument, SandboxComplexDocumentEditorVM, SandboxComplexDocumentEditorVS, SandboxComplexDocumentEditorVV> {

    override fun read(entity: SandboxComplexDocument?, vmEntity: SandboxComplexDocumentEditorVM, ctx: MutableMap<String, Any>) {
        vmEntity.generalTile = TileData()
        vmEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVM()
        vmEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVM()
        if (entity == null) {
            vmEntity.generalTile.compactData.stringProperty = "Новый объект"
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
            item.entityColumn = it.entityRefColumn
            item.enumColumn = it.enumColumn
        }
        vmEntity.variants = TileData()
        vmEntity.variants.fullData = SandboxComplexDocumentVariantsTileFullVM()
        vmEntity.variants.compactData = SandboxComplexDocumentVariantsTileCompactVM()
        entity.nestedDocuments.forEach {
            vmEntity.variants.fullData.variants.add(when (it) {
                is SandboxNavigatorVariant1 -> {
                    val res = SandboxComplexDocumentVariant1ViewVM()
                    res.uid = it.uid
                    res.caption = it.title
                    res.intValue = it.intValue
                    res.nameValue = it.title
                    res
                }
                is SandboxNavigatorVariant2 -> {
                    val res = SandboxComplexDocumentVariant2ViewVM()
                    res.uid = it.uid
                    res.caption = it.title
                    res.dateValue = it.dateValue
                    res.nameValue = it.title
                    res
                }
                else -> throw IllegalArgumentException("unsupported type $it")
            }
            )
        }
        entity.nestedDocuments.forEach {
            val res = SandboxVariantsCompactTableVM()
            res.uid = it.uid+"compact"
            res.name = it.title
            val navigation = NavigationTableColumnData()
            res.navigation = navigation
            navigation.navigationKey = it.uid
            vmEntity.variants.compactData.variants.add(res)
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
        entity.entityRefProperty = vmEntity.generalTile.fullData.entityProperty
        UiUtils.writeCollection(vmEntity.generalTile.fullData.entityCollection, entity.entityCollection, SandboxNestedDocument::class) { sandboxTableVM, sandboxNestedDocument ->
            sandboxNestedDocument.integerColumn = sandboxTableVM.integerColumn
            sandboxNestedDocument.floatColumn = sandboxTableVM.floatColumn
            sandboxNestedDocument.textColumn = sandboxTableVM.textColumn
            sandboxNestedDocument.enumColumn = sandboxTableVM.enumColumn
            sandboxNestedDocument.entityRefColumn = sandboxTableVM.entityColumn
        }
        UiUtils.writeCollection(vmEntity.variants.fullData.variants, entity.nestedDocuments, {
            when (it) {
                is SandboxComplexDocumentVariant1ViewVM -> SandboxNavigatorVariant1()
                is SandboxComplexDocumentVariant2ViewVM -> SandboxNavigatorVariant2()
                else -> throw IllegalArgumentException("unsupported type ${it}")
            }
        }, { variantVM, variant ->
            when (variantVM) {
                is SandboxComplexDocumentVariant1ViewVM -> {
                    val res = variant as SandboxNavigatorVariant1
                    res.title = variantVM.nameValue
                    res.intValue = variantVM.intValue
                }
                is SandboxComplexDocumentVariant2ViewVM -> {
                    val res = variant as SandboxNavigatorVariant2
                    res.title = variantVM.nameValue
                    res.dateValue = variantVM.dateValue
                }
                else -> throw IllegalArgumentException("unsupported type ${variantVM}")
            }
        }
        )
    }

    override fun getTitle(entity: SandboxComplexDocument?, vmEntity: SandboxComplexDocumentEditorVM, vsEntity: SandboxComplexDocumentEditorVS, ctx: MutableMap<String, Any>): String? {
        return entity?.stringProperty ?: "Новый документ"
    }

    override fun fillSettings(entity: SandboxComplexDocument?, vsEntity: SandboxComplexDocumentEditorVS, vmEntity: SandboxComplexDocumentEditorVM, ctx: MutableMap<String, Any>) {
        vsEntity.generalTile = TileData()
        vsEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVS {
            entityCollection = TableConfiguration {
                columnSettings = SandboxTableVS() {
                    enumColumn = EnumSelectConfiguration()
                    entityColumn = UiUtils.createStandardAutocompletetConfiguration((SandboxUserAccount::class))
                }
            }
            entityProperty = UiUtils.createStandardAutocompletetConfiguration((SandboxUserAccount::class))
            enumProperty = EnumSelectConfiguration()
        }
        vsEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVS()
        vsEntity.variants = TileData()
        vsEntity.variants.compactData = SandboxComplexDocumentVariantsTileCompactVS(){
            variants = TableConfiguration{
                columnSettings = SandboxVariantsCompactTableVS{

                }
            }
        }
        vsEntity.variants.fullData = SandboxComplexDocumentVariantsTileFullVS()
        vmEntity.variants.fullData.variants.forEach {
            vsEntity.variants.fullData.variants.add(when (it) {
                is SandboxComplexDocumentVariant1ViewVM -> SandboxComplexDocumentVariant1ViewVS()
                is SandboxComplexDocumentVariant2ViewVM -> SandboxComplexDocumentVariant2ViewVS()
                else -> throw IllegalArgumentException("unsupported type ${it}")
            }
            )
        }
    }

    override fun validate(vmEntity: SandboxComplexDocumentEditorVM, vvEntity: SandboxComplexDocumentEditorVV, ctx: MutableMap<String, Any>) {
        vvEntity.generalTile = TileData()
        vvEntity.generalTile.fullData = SandboxComplexDocumentGeneralTileFullVV()
        vvEntity.generalTile.compactData = SandboxComplexDocumentGeneralTileCompactVV()
        vvEntity.variants = TileData()
        vvEntity.variants.fullData = SandboxComplexDocumentVariantsTileFullVV()
        vvEntity.variants.compactData = SandboxComplexDocumentVariantsTileCompactVV()
        vmEntity.variants.fullData.variants.forEach {
            vvEntity.variants.fullData.variants.add(when (it) {
                is SandboxComplexDocumentVariant1ViewVM -> {
                    val res = SandboxComplexDocumentVariant1ViewVV()
                    if (it.nameValue == null) {
                        res.nameValue = StandardValidationMessages.EMPTY_VALUE()
                    }
                    res
                }
                is SandboxComplexDocumentVariant2ViewVM -> {
                    val res = SandboxComplexDocumentVariant2ViewVV()
                    if (it.nameValue == null) {
                        res.nameValue = StandardValidationMessages.EMPTY_VALUE()
                    }
                    res
                }
                else -> throw IllegalArgumentException("unsupported type ${it}")
            }
            )
        }
    }

}


