/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.ui


import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File
import java.util.*


object UiMetadataParser {


    fun updateUiMetaRegistry(registry: UiMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node, localizations)
    }


    fun updateUiMetaRegistry(registry: UiMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node, localizations)

    }

    private fun updateRegistry(registry: UiMetaRegistry, node: XmlNode, localizations: Map<String, Map<Locale, String>>) {
        node.children("enum").forEach { child ->
            val enumId = child.attributes["id"] ?: throw Xeption.forDeveloper("id attribute is absent in enum $child")
            val enumDescription = registry.enums.getOrPut(enumId) { UiEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = enumItemElm.attributes["id"]
                        ?: throw Xeption.forDeveloper("id attribute is absent in enum item $enumItemElm")
                val enumItem = enumDescription.items.getOrPut(enumItemId) { UiEnumItemDescription(enumItemId) }
                ParserUtils.updateLocalizations(enumItem, enumId, localizations)
            }
        }
        node.children("grid-container").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val gridContainer = registry.views.getOrPut(id, {
                GridContainerDescription(id,
                        ParserUtils.getIntegerAttribute(child, "columns-count"))
            }) as GridContainerDescription
            val viewModelId = "${id}VM"
            val viewModelEntity = registry.viewModels.getOrPut(viewModelId, {VMEntityDescription(viewModelId)})
            val viewSettigsId = "${id}VS"
            val viewSettingsEntity = registry.viewSettings.getOrPut(viewSettigsId, { VSEntityDescription(viewSettigsId) })
            val viewValidationId = "${id}VV"
            val viewValidationEntity = registry.viewValidations.getOrPut(viewValidationId, {VVEntityDescription(viewValidationId)})
            child.children("columns").forEach { columnsElm ->
                columnsElm.children("column").forEach { columnElm ->
                    val col = GridContainerColumnDescription(ParserUtils.getEnum(columnElm, "width",
                            PredefinedColumnWidth::class)?:PredefinedColumnWidth.STANDARD, columnElm.attributes["custom-width"])
                    gridContainer.columns.add(col)
                }
            }
            child.children("row").forEach { rowElm ->
                val row = GridContainerRowDescription(ParserUtils.getEnum(rowElm, "height",
                        PredefinedRowHeight::class)?:PredefinedRowHeight.AUTO, rowElm.attributes["custom-height"])
                gridContainer.rows.add(row)
                rowElm.children("cell").forEach { cellElm ->
                    val cell = GridContainerCellDescription(ParserUtils.getIdAttribute(cellElm),
                            ParserUtils.getCaptionAttribute(cellElm), ParserUtils.getIntegerAttribute(cellElm, "col-span")?:1)
                    ParserUtils.updateLocalizations(cell, localizations, cell.caption)
                    row.cells.add(cell)
                    val widgetData = parseWidgetData(cellElm.children[0], cell.id)
                    cell.widget = widgetData.widget
                    viewModelEntity.properties[widgetData.vmPropertyDescription.id] = widgetData.vmPropertyDescription
                    viewSettingsEntity.properties[widgetData.vsPropertyDescription.id] = widgetData.vsPropertyDescription
                    viewValidationEntity.properties[widgetData.vvPropertyDescription.id] = widgetData.vvPropertyDescription
                }
            }
        }
    }

    private fun parseWidgetData(xmlNode: XmlNode, id: String): WidgetParsingData {
         return when(xmlNode.name){
             "text-box" ->{
                 val widget = TextBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")?:false)
                 val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false)
                 val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.TEXT_BOX_SETTINGS, null)
                 val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null)
                 WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription)
             }
             "password-box" ->{
                 val widget = PasswordBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")?:false)
                 val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false)
                 val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.PASSWORD_BOX_SETTINGS, null)
                 val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null)
                 WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription)
             }
             else->throw IllegalArgumentException("unsupported element name ${xmlNode.name}")
         }
    }

    data class WidgetParsingData(val widget:BaseWidgetDescription, val vmPropertyDescription:VMPropertyDescription, val vsPropertyDescription: VSPropertyDescription, val vvPropertyDescription: VVPropertyDescription)
}