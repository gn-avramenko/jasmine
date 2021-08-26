/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.parser


import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.utils.XmlNode
import java.io.File
import java.util.*


object UiMetadataParser {

    fun updateWebMessages(registry: WebMessagesMetaRegistry, uiRegistry:UiMetaRegistry){
        uiRegistry.views.values.forEach {vd->
            val bundle = WebMessagesBundleDescription(vd.id)
            registry.bundles[bundle.id] = bundle
            when(vd.viewType){
                ViewType.GRID_CONTAINER ->{
                    vd as GridContainerDescription
                    vd.rows.forEach {row ->
                        row.cells.forEach{cell ->
                            val message = WebMessageDescription(cell.id)
                            message.displayNames.putAll(cell.displayNames)
                            bundle.messages[cell.id] = message
                            val widget = cell.widget
                            if(widget is TableBoxWidgetDescription){
                                val bundle2 = WebMessagesBundleDescription(widget.id)
                                registry.bundles[widget.id] = bundle2
                                widget.columns.forEach{column ->
                                    val message2 = WebMessageDescription(column.id)
                                    message2.displayNames.putAll(column.displayNames)
                                    bundle2.messages[column.id] = message2
                                }
                            }
                        }
                    }
                }
                ViewType.TILE_SPACE ->{
                    vd as TileSpaceDescription
                    vd.overviewDescription?.let {
                        val message = WebMessageDescription("overview")
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                    vd.tiles.forEach {
                        val message = WebMessageDescription(it.id)
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                }
                else -> {}
            }
        }
    }

    fun updateUiMetaRegistry(registry: UiMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node, localizations)
    }


    fun updateUiMetaRegistry(registry: UiMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node, localizations)

    }

    private fun updateRegistry(registry: UiMetaRegistry, node: XmlNode, localizations: Map<String, Map<Locale, String>>) {
        node.children("custom-value-widget").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val viewModelId = parseViewModel(child.children("view-model")[0], "${id}VM", registry)
            val viewSettingsId = parseViewSettings(child.children("view-settings")[0], "${id}VS", registry)
            val viewValidationId = parseViewValidation(child.children("view-validation")[0], "${id}VV", registry)
            registry.customValueWidgets[id] = CustomValueWidgetDescription(viewModelId, viewSettingsId,viewValidationId)
        }
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
        node.children("actions-group").forEach { child ->
            updateActionsGroup(child, registry, localizations).apply { root = true }
        }
        node.children("options-group").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val optionsGroup = registry.optionsGroups.getOrPut(id){OptionsGroupDescription(id)}
            child.children("option").forEach {optionElm ->
                val optionId = ParserUtils.getIdAttribute(optionElm)
                val option = OptionDescription(optionId)
                optionsGroup.options.add(option)
                ParserUtils.updateLocalizationsForId(option, ParserUtils.getCaptionAttribute(optionElm), localizations)
            }
        }
        node.children("display-handler").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            registry.displayHandlers[id] = DisplayHandlerDescription(id).apply {
                className = child.attributes["class-name"]!!
            }
        }
        processContainers(registry, node, null, localizations)


    }

    private fun parseViewModel(xmlNode: XmlNode, defaultId: String, registry: UiMetaRegistry): String {
        val id = xmlNode.attributes["id"]?:defaultId
        val entity = registry.viewModels.getOrPut(id){ VMEntityDescription(id)}
        xmlNode.children("property").forEach {child ->
            val childId = ParserUtils.getIdAttribute(child)
            when(VMPropertyType.valueOf(child.attributes["type"]!!)){
                VMPropertyType.STRING -> {
                    entity.properties[childId] = VMPropertyDescription(childId,VMPropertyType.STRING,  null, false, false)
                }
                else-> TODO()
            }
        }
        return id
    }
    private fun parseViewSettings(xmlNode: XmlNode, defaultId: String, registry: UiMetaRegistry): String {
        val id = xmlNode.attributes["id"]?:defaultId
        val entity = registry.viewSettings.getOrPut(id){ VSEntityDescription(id)}
        xmlNode.children("property").forEach {child ->
            val childId = ParserUtils.getIdAttribute(child)
            when(VMPropertyType.valueOf(child.attributes["type"]!!)){
                VMPropertyType.STRING -> {
                    entity.properties[childId] = VSPropertyDescription(childId,VSPropertyType.STRING,  null, false)
                }
                else-> TODO()
            }
        }
        return id
    }
    private fun parseViewValidation(xmlNode: XmlNode, defaultId: String, registry: UiMetaRegistry): String {
        val id = xmlNode.attributes["id"]?:defaultId
        val entity = registry.viewValidations.getOrPut(id){ VVEntityDescription(id)}
        xmlNode.children("property").forEach {child ->
            val childId = ParserUtils.getIdAttribute(child)
            when(VMPropertyType.valueOf(child.attributes["type"]!!)){
                VMPropertyType.STRING -> {
                    entity.properties[childId] = VVPropertyDescription(childId,VVPropertyType.STRING,  null, false)
                }
                else-> TODO()
            }
        }
        return id
    }

    private fun updateActionsGroup(elm: XmlNode, registry: UiMetaRegistry, localizations: Map<String, Map<Locale, String>>):ActionsGroupDescription {
        val groupId = ParserUtils.getIdAttribute(elm)
        val groupDescription = registry.actions.getOrPut(groupId) { ActionsGroupDescription(groupId) } as ActionsGroupDescription
        ParserUtils.updateLocalizationsForId(groupDescription, groupDescription.id, localizations)
        groupDescription.icon = groupDescription.icon?:elm.attributes["icon"]
        elm.children.forEach {child ->
            when(child.name){
                "action" ->{
                    val actionId = ParserUtils.getIdAttribute(child)
                    val action = registry.actions.getOrPut(actionId) { ActionDescription(actionId) } as ActionDescription
                    action.actionHandler = child.attributes["action-handler"]!!
                    action.displayHandlerRef = child.attributes["display-handler-ref"]
                    action.icon = child.attributes["icon"]
                    ParserUtils.updateLocalizationsForId(action, actionId, localizations)
                    groupDescription.actionsIds.add(actionId)
                }
                "group" ->{
                    updateActionsGroup(child, registry, localizations).also {
                        groupDescription.actionsIds.add(it.id)
                    }
                }
                "group-ref","action-ref" ->  groupDescription.actionsIds.add(ParserUtils.getIdAttribute(child))
            }
        }
        return groupDescription
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
            gridContainer.interceptors.addAll(child.children("interceptor").mapNotNull { it.value })
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
                viewModelEntity.properties["overview"] = VMPropertyDescription("overview", VMPropertyType.ENTITY, "${it.viewId}VM", false, false)
                viewSettingsEntity.properties["overview"] = VSPropertyDescription("overview", VSPropertyType.ENTITY, "${it.viewId}VS", false)
                viewValidationEntity.properties["overview"] = VVPropertyDescription("overview", VVPropertyType.ENTITY, "${it.viewId}VV", false)
                it
            }
            val res = registry.views.getOrPut(id, {
                TileSpaceDescription(id, overviewDescription)
            }) as TileSpaceDescription
            res.interceptors.addAll(child.children("interceptor").mapNotNull { it.value })
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
                viewSettingsEntity.properties[tileId] = VSPropertyDescription(tileId, VSPropertyType.ENTITY, "${descr.fullViewId}VS", false)
                viewValidationEntity.properties[tileId] = VVPropertyDescription(tileId, VVPropertyType.ENTITY, "${descr.fullViewId}VV", false)
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
            res.interceptors.addAll(child.children("interceptor").mapNotNull { it.value })
            child.children("variant").forEach { navigatorElm ->
                val containerRef = navigatorElm.attributes["container-ref"]
                val descr = if (containerRef != null) {
                    NavigatorVariantDescription("${containerRef}VM", containerRef)
                } else {
                    val containerId = processContainers(registry, navigatorElm, BaseNavigatorVariantVM::class.qualifiedName?.substringBeforeLast("VM"), localizations)[0]
                    val cont = registry.views[containerId]
                    if(cont is GridContainerDescription){
                        val uidCell = GridContainerCellDescription("uid", "", 1)
                        uidCell.widget = HiddenWidgetDescription("String", true)
                        cont.rows[0].cells.add(uidCell)
                        val titleCell = GridContainerCellDescription("title", "", 1)
                        titleCell.widget = HiddenWidgetDescription("String", true)
                        cont.rows[0].cells.add(titleCell)
                    }
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
                val widget = TextBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable") ?: false,ParserUtils.getBooleanAttribute(xmlNode, "multiline") ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false, false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.TEXT_BOX_SETTINGS, null, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.STRING, null, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "rich-text-editor" -> {
                val widget = RichTextBoxEditorDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable") ?: false, xmlNode.attributes["height"])
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.STRING, null, false, false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.RICH_TEXT_EDITOR_SETTINGS, null, false)
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
            "big-decimal-number-box" -> {
                val widget = BigDecimalNumberBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
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
            "general-select-box" -> {
                val widget = GeneralSelectBoxWidgetDescription(ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.ENTITY, SelectItem::class.qualifiedName, false,false)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.GENERAL_SELECT_BOX_SETTINGS, null, false)
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
            "custom-value-widget" -> {
                val widgetId = xmlNode.attributes["ref"]!!
                val widgetDescr = registry.customValueWidgets[widgetId]
                val viewModel = widgetDescr?.viewModel?:"${widgetId}VM"
                val viewSettings = widgetDescr?.viewSettings?:"${widgetId}VS"
                val viewValidation = widgetDescr?.viewValidation?:"${widgetId}VV"
                val params = linkedMapOf(*xmlNode.children("param").map { it.name to it.value!! }.toTypedArray())
                val widget = CustomValueWidgetRef(widgetId, params)
                val vmPropertyDescription = VMPropertyDescription(id, VMPropertyType.ENTITY, viewModel, false,true)
                val vsPropertyDescription = VSPropertyDescription(id, VSPropertyType.ENTITY, viewSettings, false)
                val vvPropertyDescription = VVPropertyDescription(id, VVPropertyType.ENTITY, viewValidation, false)
                WidgetParsingData(widget, vmPropertyDescription, vsPropertyDescription, vvPropertyDescription, null, null, null)
            }
            "hidden" -> {
                val widget = HiddenWidgetDescription(xmlNode.attributes["object-id"]!!,"true" == xmlNode.attributes["non-nullable"])
                val vmPropertyDescription = when(widget.objectId){
                    "String" ->VMPropertyDescription(id, VMPropertyType.STRING, null, false,false)
                    else -> TODO()
                }
                WidgetParsingData(widget, vmPropertyDescription, null, null, null, null, null)
            }
            "table-box" -> {
                val widget = TableBoxWidgetDescription(ParserUtils.getIdAttribute(xmlNode), ParserUtils.getBooleanAttribute(xmlNode, "not-editable")
                        ?: false)
                val viewModelId = "${widget.id}VM"
                val viewModelEntity = registry.viewModels.getOrPut(viewModelId, {
                    val ett = VMEntityDescription(viewModelId)
                    ett.extendsId = BaseTableBoxVM::class.qualifiedName
                    ett
                })
                val viewSettigsId = "${widget.id}VS"
                val viewSettingsEntity = registry.viewSettings.getOrPut(viewSettigsId, {
                    val ett = VSEntityDescription(viewSettigsId)
                    ett.extendsId = BaseTableBoxVS::class.qualifiedName
                    ett
                })
                val viewValidationId = "${widget.id}VV"
                val viewValidationEntity = registry.viewValidations.getOrPut(viewValidationId, {
                    val ett = VVEntityDescription(viewValidationId)
                    ett.extendsId = BaseTableBoxVV::class.qualifiedName
                    ett
                })
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