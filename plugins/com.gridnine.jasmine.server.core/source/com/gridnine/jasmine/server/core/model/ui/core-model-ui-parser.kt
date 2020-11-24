/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.ui


import com.gridnine.jasmine.server.core.model.common.BaseIdentity
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
        processContainers(registry, node, null, localizations)


    }

    private fun processContainers(registry: UiMetaRegistry, node: XmlNode, baseExtendsId:String?, localizations: Map<String, Map<Locale, String>>): List<String> {
        val result = arrayListOf<String>()
        node.children("grid-container").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            result.add(id)
            val gridContainer = registry.views.getOrPut(id, {
                GridContainerDescription(id,
                        ParserUtils.getIntegerAttribute(child, "columns-count"))
            }) as GridContainerDescription
            val viewModelId = "${id}VM"
            val viewModelEntity = registry.viewModels.getOrPut(viewModelId, { VMEntityDescription(viewModelId) })
            baseExtendsId?.let { viewModelEntity.extendsId = it+"VM"}
            val viewSettigsId = "${id}VS"
            val viewSettingsEntity = registry.viewSettings.getOrPut(viewSettigsId, { VSEntityDescription(viewSettigsId) })
            baseExtendsId?.let { viewSettingsEntity.extendsId = it+"VS"}
            val viewValidationId = "${id}VV"
            val viewValidationEntity = registry.viewValidations.getOrPut(viewValidationId, { VVEntityDescription(viewValidationId) })
            baseExtendsId?.let { viewValidationEntity.extendsId = it+"VV"}
            child.children("columns").forEach { columnsElm ->
                columnsElm.children("column").forEach { columnElm ->
                    val col = GridContainerColumnDescription(ParserUtils.getEnum(columnElm, "width",
                            PredefinedColumnWidth::class)
                            ?: PredefinedColumnWidth.STANDARD, columnElm.attributes["custom-width"])
                    gridContainer.columns.add(col)
                }
            }
            child.children("row").forEach { rowElm ->
                val row = GridContainerRowDescription(ParserUtils.getEnum(rowElm, "height",
                        PredefinedRowHeight::class) ?: PredefinedRowHeight.AUTO, rowElm.attributes["custom-height"])
                gridContainer.rows.add(row)
                rowElm.children("cell").forEach { cellElm ->
                    val cell = GridContainerCellDescription(ParserUtils.getIdAttribute(cellElm),
                            cellElm.attributes["caption"], ParserUtils.getIntegerAttribute(cellElm, "col-span")
                            ?: 1)
                    cell.caption?.let {  ParserUtils.updateLocalizations(cell, localizations, it)}
                    row.cells.add(cell)
                    val widgetData = parseWidgetData(cellElm.children[0], cell.id, registry, localizations)
                    cell.widget = widgetData.widget
                    widgetData.vmPropertyDescription?.let{viewModelEntity.properties[it.id] = it}
                    widgetData.vsPropertyDescription?.let{viewSettingsEntity.properties[it.id] = it}
                    widgetData.vvPropertyDescription?.let{viewValidationEntity.properties[it.id] = it}
                    widgetData.vmCollectionDescription?.let{viewModelEntity.collections[it.id] = it}
                    widgetData.vsCollectionDescription?.let{viewSettingsEntity.collections[it.id] = it}
                    widgetData.vvCollectionDescription?.let{viewValidationEntity.collections[it.id] = it}

                }
            }
        }
        node.children("tile-space").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            result.add(id)
            val viewModelId = "${id}VM"
            val viewModelEntity = registry.viewModels.getOrPut(viewModelId, { VMEntityDescription(viewModelId) })
            val viewSettigsId = "${id}VS"
            val viewSettingsEntity = registry.viewSettings.getOrPut(viewSettigsId, { VSEntityDescription(viewSettigsId) })
            val viewValidationId = "${id}VV"
            val viewValidationEntity = registry.viewValidations.getOrPut(viewValidationId, { VVEntityDescription(viewValidationId) })
            val overviewDescription = child.children("overview").map { overviewElm ->
                val descr = if (overviewElm.attributes.containsKey("container-ref")) {
                    TileSpaceOverviewDescription(overviewElm.attributes["container-ref"]!!)
                } else {
                    val containerId = processContainers(registry, overviewElm, null, localizations)[0]
                    TileSpaceOverviewDescription(containerId)
                }
                ParserUtils.updateLocalizations(descr, localizations, ParserUtils.getCaptionAttribute(overviewElm))
                descr
            }.firstOrNull()?.let {
                val view = registry.views[it.viewId]!!
                viewModelEntity.properties["overview"] = VMPropertyDescription("overview", VMPropertyType.ENTITY, "${it.viewId}VM", false, false)
                viewSettingsEntity.properties["overview"] = VSPropertyDescription("overview", VSPropertyType.ENTITY, "${it.viewId}VS", false)
                viewValidationEntity.properties["overview"] = VVPropertyDescription("overview", VVPropertyType.ENTITY, "${it.viewId}VV", false)
                it
            }
            val res = registry.views.getOrPut(id, {
                TileSpaceDescription(id, overviewDescription)
            }) as TileSpaceDescription
            child.children("tile").forEach { tileElm ->
                val tileId = ParserUtils.getIdAttribute(tileElm)
                val fullViewElm = tileElm.children("full-view")[0]
                val descr = if (fullViewElm.attributes.containsKey("container-ref")) {
                    TileDescription(tileId, fullViewElm.attributes["container-ref"]!!)
                } else {
                    val containerId = processContainers(registry, fullViewElm, null, localizations)[0]
                    TileDescription(tileId, containerId)
                }
                ParserUtils.updateLocalizations(descr, localizations, ParserUtils.getCaptionAttribute(tileElm))
                res.tiles.add(descr)
                viewModelEntity.properties[tileId] = VMPropertyDescription(tileId, VMPropertyType.ENTITY, "${descr.fullViewId}VM", false, true)
                viewSettingsEntity.properties[tileId] = VSPropertyDescription(tileId, VSPropertyType.ENTITY, "${descr.fullViewId}VS", true)
                viewValidationEntity.properties[tileId] = VVPropertyDescription(tileId, VVPropertyType.ENTITY, "${descr.fullViewId}VV", true)
            }
        }
        node.children("navigator").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            result.add(id)
            val viewModelId = "${id}VM"
            registry.viewModels.computeIfAbsent(viewModelId){
                val ett = VMEntityDescription(viewModelId)
                ett.collections["values"] = VMCollectionDescription("values", VMCollectionType.ENTITY, BaseNavigatorVariantVM::class.qualifiedName)
                ett
            }
            val viewSettigsId = "${id}VS"
            registry.viewSettings.computeIfAbsent(viewSettigsId){
                val ett = VSEntityDescription(viewSettigsId)
                ett.collections["values"] = VSCollectionDescription("values", VSCollectionType.ENTITY,  BaseNavigatorVariantVS::class.qualifiedName)
                ett
            }
            val viewValidationId = "${id}VV"
            registry.viewValidations.computeIfAbsent(viewValidationId){
                val ett = VVEntityDescription(viewValidationId)
                ett.collections["values"] = VVCollectionDescription("values", VVCollectionType.ENTITY,  BaseNavigatorVariantVV::class.qualifiedName)
                ett
            }
            val res = registry.views.getOrPut(id, {
                NavigatorDescription(id)
            }) as NavigatorDescription
            child.children("variant").forEach { navigatorElm ->
                val containerRef = navigatorElm.attributes["container-ref"]
                val descr = if (containerRef != null) {
                    NavigatorVariantDescription("${containerRef}VM", containerRef)
                } else {
                    val containerId = processContainers(registry, navigatorElm, BaseNavigatorVariantVM::class.qualifiedName?.substringBeforeLast("VM"), localizations)[0]
                    NavigatorVariantDescription("${containerId}VM", containerId)
                }
                res.variants.add(descr)
            }
        }
        return result
    }


    private fun parseWidgetData(xmlNode: XmlNode, id: String, registry: UiMetaRegistry, localizations: Map<String, Map<Locale, String>>): WidgetParsingData {
        return when (xmlNode.name) {
            "text-box" -> {
                val widget = TextBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable") ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false, false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.TEXT_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "password-box" -> {
                val widget = PasswordBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.PASSWORD_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "float-number-box" -> {
                val widget = FloatNumberBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.BIG_DECIMAL, null, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "integer-number-box" -> {
                val widget = IntegerNumberBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false, ParserUtils.getBooleanAttribute(xmlNode, "non-nullable")?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.INT, null, widget.nonNullable,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "boolean-box" -> {
                val widget = BooleanBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.BOOLEAN, null, true,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.BOOLEAN_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "entity-select-box" -> {
                val widget = EntitySelectBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false, xmlNode.attributes["objectId"]!!)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.ENTITY_REFERENCE, widget.objectId, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.ENTITY_SELECT_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "enum-select-box" -> {
                val widget = EnumSelectBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false, xmlNode.attributes["enumId"]!!)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.ENUM, widget.enumId, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.ENUM_SELECT_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "date-box" -> {
                val widget = DateBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.LOCAL_DATE, null, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.DATE_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "date-time-box" -> {
                val widget = DateTimeBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.LOCAL_DATE_TIME, null, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.DATE_TIME_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "table-box" -> {
                val widget = TableBoxWidgetDescription(ParserUtils.getIdAttribute(xmlNode), ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val viewModelId = "${widget.id}VM"
                val viewModelEntity = registry.viewModels.getOrPut(viewModelId, { VMEntityDescription(viewModelId) })
                val viewSettigsId = "${widget.id}VS"
                val viewSettingsEntity = registry.viewSettings.getOrPut(viewSettigsId, { VSEntityDescription(viewSettigsId) })
                val viewValidationId = "${widget.id}VV"
                val viewValidationEntity = registry.viewValidations.getOrPut(viewValidationId, { VVEntityDescription(viewValidationId) })
                viewModelEntity.properties[BaseIdentity.uid] = VMPropertyDescription(BaseIdentity.uid, VMPropertyType.STRING, null, false, true)
                viewSettingsEntity.properties[BaseIdentity.uid] = VSPropertyDescription(BaseIdentity.uid, VSPropertyType.STRING, null, true)
                viewValidationEntity.properties[BaseIdentity.uid] = VVPropertyDescription(BaseIdentity.uid, VVPropertyType.STRING, null, true)
                xmlNode.children("column").forEach { columnElm ->
                    val columnId =  ParserUtils.getIdAttribute(columnElm)
                    val widgetData = parseWidgetData(columnElm.children[0], columnId, registry, localizations)
                    widgetData.vmPropertyDescription?.let{viewModelEntity.properties[it.id] = it}
                    widgetData.vsPropertyDescription?.let{viewSettingsEntity.properties[it.id] = it}
                    widgetData.vvPropertyDescription?.let{viewValidationEntity.properties[it.id] = it}
                    widgetData.vmCollectionDescription?.let{viewModelEntity.collections[it.id] = it}
                    widgetData.vsCollectionDescription?.let{viewSettingsEntity.collections[it.id] = it}
                    widgetData.vvCollectionDescription?.let{viewValidationEntity.collections[it.id] = it}
                    val columnDescription = TableColumnDescription(columnId, columnElm.attributes["pref-width"], widgetData.widget)
                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnElm))
                    widget.columns.add(columnDescription)
                }
                val vmCollectionDescription = VMCollectionDescription(id, VMCollectionType.ENTITY, "${widget.id}VM")
                val vsCollectionDescription = VSCollectionDescription(id, VSCollectionType.ENTITY, "${widget.id}VS")
                val vvCollectionDescription = VVCollectionDescription(id, VVCollectionType.ENTITY, "${widget.id}VV")
                WidgetParsingData(widget, null, null, null, vmCollectionDescription,vsCollectionDescription, vvCollectionDescription)
            }
            else -> throw IllegalArgumentException("unsupported element name ${xmlNode.name}")
        }
    }

    data class WidgetParsingData(val widget: BaseWidgetDescription, val vmPropertyDescription: VMPropertyDescription?, val vsPropertyDescription: VSPropertyDescription?, val vvPropertyDescription: VVPropertyDescription?
                                 , val vmCollectionDescription: VMCollectionDescription?, val vsCollectionDescription: VSCollectionDescription?, val vvCollectionDescription: VVCollectionDescription?)
}