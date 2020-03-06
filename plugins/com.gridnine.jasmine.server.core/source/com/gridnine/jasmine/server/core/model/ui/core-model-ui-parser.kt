/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File
import java.util.*


object UiMetadataParser {

    fun updateUiMetaRegistry(registry: UiMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateUiMetaRegistry(registry, node, localizations)
    }


    fun updateUiMetaRegistry(registry: UiMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateUiMetaRegistry(registry, node, localizations)
    }

    private fun updateUiMetaRegistry(registry: UiMetaRegistry, node:XmlNode, localizations: Map<String, Map<Locale, String>>) {
        node.children("validation-messages").forEach { child ->
            val enumId = child.attributes["id"]?:throw IllegalArgumentException("element ${child.name} has no id attribute")
            val enumDesc = ValidationMessagesEnumDescription(enumId)
            child.children("message").forEach {
                val itemId = it.attributes["id"]?:throw IllegalArgumentException("element ${it.name} has no id attribute")
                val enumItem = ValidationMessageDescription(enumId, itemId)
                ParserUtils.updateLocalizations(enumItem, localizations)
                enumDesc.items[itemId] = enumItem
            }
            registry.validationMessages[enumId] = enumDesc
        }
        node.children("shared-editor-tool-button").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val button = SharedEditorToolButtonDescription(id, child.attributes["handler"]?:
            throw IllegalArgumentException("${child.name} has no handler attribute"),
                    child.attributes["weight"]?.toDouble()?:
                    throw IllegalArgumentException("${child.name} has no weight attribute"))
            ParserUtils.updateLocalizations(button, localizations, ParserUtils.getCaptionAttribute(child))
            registry.sharedEditorToolButtons.add(button)
        }
        node.children("shared-list-tool-button").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val button = SharedListToolButtonDescription(id, child.attributes["handler"]?:
            throw IllegalArgumentException("${child.name} has no handler attribute"),
                    child.attributes["weight"]?.toDouble()?:
                    throw IllegalArgumentException("${child.name} has no weight attribute"))
            ParserUtils.updateLocalizations(button, localizations, ParserUtils.getCaptionAttribute(child))
            registry.sharedListToolButtons.add(button)
        }
        node.children("autocomplete").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val autocomplete = AutocompleteDescription(id,
                    child.attributes["entity"]?: throw IllegalArgumentException("${child.name} has no entity attribute"),
                    getSortProperty(child),
                    getSortOrder(child),
                    child.attributes["criterionsProvider"]
            )
            child.children("columns")[0].children("column").forEach {
                autocomplete.columns.add(it.value!!)
            }
            val filters = child.children("filters")
            if(filters.isNotEmpty()){
                filters[0].children("filter").forEach { autocomplete.filters.add(it.value!!)}
            }
            ParserUtils.updateLocalizations(autocomplete, localizations, id)
            registry.autocompletes[id] = autocomplete
        }
        node.children("list").forEach { listElm ->
            val listId = ParserUtils.getIdAttribute(listElm)
            val descr = ListDescription(listId, listElm.attributes["object-id"]?:throw IllegalArgumentException("${listElm.name} has no object-id attribute"))
            registry.lists[listId] = descr
            listElm.children("toolbar").firstOrNull()?.children("tool-button")?.forEach {toolButtonElm ->
                val buttonId = ParserUtils.getIdAttribute(toolButtonElm)
                val button = ListToolButtonDescription(listId, buttonId, toolButtonElm.attributes["handler"]?:
                throw IllegalArgumentException("${toolButtonElm.name} has no handler attribute"),
                        toolButtonElm.attributes["weight"]?.toDouble()?:
                        throw IllegalArgumentException("${toolButtonElm.name} has no weight attribute"))
                ParserUtils.updateLocalizations(button, localizations, ParserUtils.getCaptionAttribute(toolButtonElm))
                descr.toolButtons.add(button)

            }
        }
        node.children("editor").forEach { child ->
            val editorId = child.attributes["id"]?:throw IllegalArgumentException("element ${child.name} has no id attribute")
            val editor = EditorDescription(editorId, child.attributes["entity-id"]?:throw IllegalArgumentException("element ${child.name} has no id entity-id"),"${editorId}View")
            child.children("editor-handler").forEach { entityHandler ->
                editor.handlers.add(entityHandler.value?:throw IllegalArgumentException("element ${entityHandler.name} has value"))
            }
            registry.editors[editorId] = editor
            child.children("toolbar").firstOrNull()?.children("tool-button")?.forEach {toolButtonElm ->
                val id = ParserUtils.getIdAttribute(toolButtonElm)
                val button = EditorToolButtonDescription(editorId, id, toolButtonElm.attributes["handler"]?:
                throw IllegalArgumentException("${toolButtonElm.name} has no handler attribute"),
                        toolButtonElm.attributes["weight"]?.toDouble()?:
                        throw IllegalArgumentException("${toolButtonElm.name} has no weight attribute"))
                ParserUtils.updateLocalizations(button, localizations, ParserUtils.getCaptionAttribute(toolButtonElm))
                editor.toolButtons.add(button)

            }
            val viewElm = child.children("view")[0]
            val vmEntityDescr = VMEntityDescription("${editorId}VM")
            val vsEntityDescr = VSEntityDescription("${editorId}VS")
            val vvEntityDescr = VVEntityDescription("${editorId}VV")
            updateViews(registry, vmEntityDescr, vsEntityDescr, vvEntityDescr,  viewElm, editorId, localizations)
            registry.viewModels[vmEntityDescr.id] = vmEntityDescr
            registry.viewSettings[vsEntityDescr.id] = vsEntityDescr
            registry.viewValidations[vvEntityDescr.id] = vvEntityDescr
        }
        node.children("dialog").forEach { child ->
            val dialogId = child.attributes["id"]?:throw IllegalArgumentException("element ${child.name} has no id attribute")
            val dialog = DialogDescription(dialogId, "${dialogId}View")
            dialog.closable = "false" != child.attributes["closable"]
            ParserUtils.updateLocalizations(dialog, localizations)
            registry.dialogs[dialogId] = dialog
            child.children("buttons").firstOrNull()?.children("button")?.forEach {dialogButton ->
                val id = ParserUtils.getIdAttribute(dialogButton)
                val button = DialogToolButtonDescription(dialogId, id, dialogButton.attributes["handler"]?:
                throw IllegalArgumentException("${dialogButton.name} has no handler attribute"),
                        dialogButton.attributes["caption"]?:
                        throw IllegalArgumentException("${dialogButton.name} has no caption attribute"))
                ParserUtils.updateLocalizations(button, localizations, ParserUtils.getCaptionAttribute(dialogButton))
                dialog.buttons.add(button)

            }
            val viewElm = child.children("view")[0]
            val vmEntityDescr = VMEntityDescription("${dialogId}VM")
            val vsEntityDescr = VSEntityDescription("${dialogId}VS")
            val vvEntityDescr = VVEntityDescription("${dialogId}VV")
            updateViews(registry, vmEntityDescr, vsEntityDescr, vvEntityDescr,  viewElm, dialogId, localizations)
            registry.viewModels[vmEntityDescr.id] = vmEntityDescr
            registry.viewSettings[vsEntityDescr.id] = vsEntityDescr
            registry.viewValidations[vvEntityDescr.id] = vvEntityDescr
        }
    }

    private fun getSortProperty(child: XmlNode): String {
        val elm = child.children("sortOrder")[0]
        return elm.attributes["property"]!!
    }
    private fun getSortOrder(child: XmlNode): AutocompleteSortOrder {
        val elm = child.children("sortOrder")[0]
        return AutocompleteSortOrder.valueOf(elm.attributes["order"]!!)
    }


    private fun updateViews(registry: UiMetaRegistry, vmEntityDescr: VMEntityDescription, vsEntityDescr: VSEntityDescription,
                            vvEntityDescr: VVEntityDescription,  viewElm: XmlNode, editorId: String, localizations: Map<String, Map<Locale, String>>) :BaseViewDescription{
        lateinit var layout:BaseLayoutDescription
        val additionalData = viewElm.attributes["additionalDataClass"]
        if(additionalData != null){
            vmEntityDescr.properties["additionalData"] = VMPropertyDescription(vmEntityDescr.id, "additionalData", VMPropertyType.ENTITY, additionalData, true)
        }
        val interceptors = arrayListOf<String>()
        viewElm.children.forEach {layoutNode ->
            if(layoutNode.name == "interceptor"){
                interceptors.add(layoutNode.value!!)
                return@forEach
            }
            if(layoutNode.name == "table-layout"){
                layout = TableLayoutDescription(layoutNode.attributes["expandLastRow"] == "true")
                val tableLayout = layout as TableLayoutDescription
                layoutNode.children("column").forEach {
                    tableLayout.columns.add(TableColumnDescription(it.attributes["width"]))
                }
            }
            layoutNode.children.forEach {
                val viewPropertyNonNullable = "true" == it.attributes["nonNullable"]
                when(it.name){
                    "text-box" ->{
                        val tb = TextboxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(tb, it)
                        updateNotEditable(tb, it)
                        layout.widgets[tb.id] = tb
                        vmEntityDescr.properties[tb.id] = VMPropertyDescription(editorId, tb.id, VMPropertyType.STRING, null, false)
                        vvEntityDescr.properties[tb.id] = VVPropertyDescription(editorId, tb.id, VVPropertyType.STRING, null)
                    }
                    "password-box" ->{
                        val tb = PasswordBoxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(tb, it)
                        updateNotEditable(tb, it)
                        layout.widgets[tb.id] = tb
                        vmEntityDescr.properties[tb.id] = VMPropertyDescription(editorId, tb.id, VMPropertyType.STRING, null, false)
                        vvEntityDescr.properties[tb.id] = VVPropertyDescription(editorId, tb.id, VVPropertyType.STRING, null)
                    }
                    "enum-select" ->{
                        val es = EnumSelectDescription(editorId, ParserUtils.getIdAttribute(it), it.attributes["enum-id"]?:throw IllegalArgumentException("${it.name} has no enum-id attribute"))
                        updateHspan(es, it)
                        updateNotEditable(es, it)
                        layout.widgets[es.id] = es
                        val vmp = VMPropertyDescription(editorId, es.id, VMPropertyType.ENUM, es.enumId, false)
                        vmEntityDescr.properties[es.id] = vmp
                        vsEntityDescr.properties[es.id] = VSPropertyDescription(editorId, es.id, VSPropertyType.ENUM_SELECT, es.enumId)
                        vvEntityDescr.properties[es.id] = VVPropertyDescription(editorId, es.id, VVPropertyType.STRING, null)
                    }
                    "select" ->{
                        val es = SelectDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(es, it)
                        updateNotEditable(es, it)
                        layout.widgets[es.id] = es
                        val vmp = VMPropertyDescription(editorId, es.id, VMPropertyType.SELECT, null, false)
                        vmEntityDescr.properties[es.id] = vmp
                        vsEntityDescr.properties[es.id] = VSPropertyDescription(editorId, es.id, VSPropertyType.SELECT, null)
                        vvEntityDescr.properties[es.id] = VVPropertyDescription(editorId, es.id, VVPropertyType.STRING, null)
                    }
                    "entity-select" ->{
                        val es = EntitySelectDescription(editorId, ParserUtils.getIdAttribute(it), it.attributes["entity-class-name"]?:throw IllegalArgumentException("${it.name} has no entity-class-name attribute"))
                        updateNotEditable(es, it)
                        updateHspan(es, it)
                        layout.widgets[es.id] = es
                        val vmp = VMPropertyDescription(editorId, es.id, VMPropertyType.ENTITY_REFERENCE, es.entityClassName,false)
                        vmEntityDescr.properties[es.id] = vmp
                        vsEntityDescr.properties[es.id] = VSPropertyDescription(editorId, es.id, VSPropertyType.ENTITY_AUTOCOMPLETE, es.entityClassName)
                        vvEntityDescr.properties[es.id] = VVPropertyDescription(editorId, es.id, VVPropertyType.STRING, null)
                    }
                    "text-area" ->{
                        val ta = TextAreaDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.STRING, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "integer-box" ->{
                        val ta = IntegerBoxDescription(editorId, ParserUtils.getIdAttribute(it), viewPropertyNonNullable )
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.INT, null,viewPropertyNonNullable)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "float-box" ->{
                        val ta = FloatBoxDescription(editorId, ParserUtils.getIdAttribute(it), viewPropertyNonNullable)
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        ParserUtils.updateLocalizations(ta, localizations)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.BIG_DECIMAL, null,viewPropertyNonNullable)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "date-box" ->{
                        val ta = DateboxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.LOCAL_DATE, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "date-time-box" ->{
                        val ta = DateTimeBoxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.LOCAL_DATE_TIME, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "boolean-box" ->{
                        val ta = BooleanBoxDescription(editorId, ParserUtils.getIdAttribute(it), viewPropertyNonNullable)
                        updateHspan(ta, it)
                        updateNotEditable(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.BOOLEAN, null,viewPropertyNonNullable)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "next-column" ->{
                        val ta = TableNextColumnDescription(editorId, UUID.randomUUID().toString())
                        layout.widgets[ta.id] = ta
                    }
                    "next-row" ->{
                        val ta = TableNextRowDescription(editorId, UUID.randomUUID().toString())
                        layout.widgets[ta.id] = ta
                    }
                    "label" ->{
                        val vAlign = it.attributes["v-align"]?.let {attr -> VerticalAlignment.valueOf(attr)}
                        val hAlign = it.attributes["h-align"]?.let {attr -> HorizontalAlignment.valueOf(attr)}
                        val ta = LabelDescription(editorId, ParserUtils.getIdAttribute(it), vAlign, hAlign)
                        updateHspan(ta, it)
                        ParserUtils.updateLocalizations(ta, localizations, ParserUtils.getCaptionAttribute(it))
                        layout.widgets[ta.id] = ta
                    }
                    "table" ->{
                        val baseClassName = it.attributes["class-name"]?:throw IllegalArgumentException("${it.name} has no class-name attribute")
                        val tableDescription = TableDescription(editorId, ParserUtils.getIdAttribute(it),baseClassName)
                        tableDescription.additionalRowDataClass =it.attributes["additional-row-data-class"]
                        updateNotEditable(tableDescription, it)
                        updateHspan(tableDescription, it)
                        layout.widgets[tableDescription.id] = tableDescription
                        val tableVM = "${baseClassName}VM".let { vmId -> registry.viewModels.getOrPut(vmId){VMEntityDescription(vmId)}}
                        val tableVS = "${baseClassName}VS".let { vsId -> registry.viewSettings.getOrPut(vsId){VSEntityDescription(vsId)}}
                        val tableVV = "${baseClassName}VV".let { vvId -> registry.viewValidations.getOrPut(vvId){VVEntityDescription(vvId)}}
                        if(tableDescription.additionalRowDataClass != null){
                            tableVM.properties["additionalData"] = VMPropertyDescription(editorId, "additionalData", VMPropertyType.ENTITY, tableDescription.additionalRowDataClass,false)
                        }
                        vmEntityDescr.collections[tableDescription.id] = VMCollectionDescription(editorId, tableDescription.id,VMCollectionType.ENTITY, tableVM.id)
                        vsEntityDescr.properties[tableDescription.id] = VSPropertyDescription(editorId, tableDescription.id, VSPropertyType.ENTITY, "${TableConfiguration::class.qualifiedName}<${tableVS.id}>")
                        vvEntityDescr.collections[tableDescription.id] = VVCollectionDescription(editorId, tableDescription.id, VVCollectionType.ENTITY, tableVV.id)
                        it.children[0].children.forEach { columnNode ->
                            val id = ParserUtils.getIdAttribute(columnNode)
                            val width = columnNode.attributes["width"]?.toInt()
                            val columnPropertyNonNullable = "true" == columnNode.attributes["nonNullable"]

                            when (columnNode.name) {
                                "text-column" -> {
                                    val columnDescription = TextTableColumnDescription(tableDescription.fullId, id)
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    columnDescription.width = width
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.STRING, null,false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                }
                                "integer-column" -> {
                                    val columnDescription = IntegerTableColumnDescription(tableDescription.fullId, id, columnPropertyNonNullable)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.INT, null,columnPropertyNonNullable)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                }
                                "float-column" -> {
                                    val columnDescription = FloatTableColumnDescription(tableDescription.fullId, id, columnPropertyNonNullable)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.BIG_DECIMAL, null, columnPropertyNonNullable )
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                }
                                "enum-select-column" -> {
                                    val columnDescription = EnumTableColumnDescription(tableDescription.fullId, id, columnNode.attributes["enum-id"]
                                            ?:throw IllegalArgumentException("${columnNode.name} has no enum-id attribute"))
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.ENUM, columnDescription.enumId, false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.ENUM_SELECT, columnDescription.enumId)
                                }
                                "entity-select-column" -> {
                                    val columnDescription = EntityTableColumnDescription(tableDescription.fullId, id, columnNode.attributes["entity-class-name"]
                                            ?:throw IllegalArgumentException("${columnNode.name} has no entity-class-name attribute"))
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.ENTITY_REFERENCE, columnDescription.entityClassName,false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.ENTITY_AUTOCOMPLETE, columnDescription.entityClassName)
                                }
                                "date-column" -> {
                                    val columnDescription = DateTableColumnDescription(tableDescription.fullId, id)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.LOCAL_DATE, null, false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                }
                                "navigation-column" -> {
                                    val columnDescription = NavigationTableColumnDescription(tableDescription.fullId, id)
                                    ParserUtils.updateLocalizations(columnDescription, localizations, ParserUtils.getCaptionAttribute(columnNode))
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.ENTITY, NavigationTableColumnData::class.qualifiedName, false)
                                }
                            }
                        }
                    }
                    "tile" ->{
                        val baseClassName = it.attributes["class-name"]?:throw IllegalArgumentException("${it.name} has no class-name attribute")
                        val compactView = run{
                            val compactViewNode = it.children("compact-view")[0].children[0]
                            val compactVM = VMEntityDescription("${baseClassName}CompactVM")
                            val compactVS = VSEntityDescription("${baseClassName}CompactVS")
                            val compactVV = VVEntityDescription("${baseClassName}CompactVV")
                            registry.viewModels[compactVM.id] = compactVM
                            registry.viewSettings[compactVS.id] = compactVS
                            registry.viewValidations[compactVV.id] = compactVV
                            updateViews(registry, compactVM, compactVS, compactVV,  compactViewNode, "${baseClassName}Compact", localizations)
                        }
                        val fullView = run{
                            val fullViewNode = it.children("full-view")[0].children[0]
                            val fullVM = VMEntityDescription("${baseClassName}FullVM")
                            val fullVS = VSEntityDescription("${baseClassName}FullVS")
                            val fullVV = VVEntityDescription("${baseClassName}FullVV")
                            registry.viewModels[fullVM.id] = fullVM
                            registry.viewSettings[fullVS.id] = fullVS
                            registry.viewValidations[fullVV.id] = fullVV
                            updateViews(registry, fullVM, fullVS, fullVV,  fullViewNode, "${baseClassName}Full", localizations)
                        }
                        val tileDescription = TileDescription(editorId, ParserUtils.getIdAttribute(it),baseClassName, compactView,fullView)
                        updateNotEditable(tileDescription, it)
                        updateHspan(tileDescription, it)
                        ParserUtils.updateLocalizations(tileDescription, localizations, ParserUtils.getCaptionAttribute(it))
                        layout.widgets[tileDescription.id] = tileDescription

                        vmEntityDescr.properties[tileDescription.id] = VMPropertyDescription(editorId, tileDescription.id,VMPropertyType.ENTITY, "${TileData::class.qualifiedName}<${baseClassName}CompactVM, ${baseClassName}FullVM>", true)
                        vsEntityDescr.properties[tileDescription.id] = VSPropertyDescription(editorId, tileDescription.id,VSPropertyType.ENTITY, "${TileData::class.qualifiedName}<${baseClassName}CompactVS, ${baseClassName}FullVS>")
                        vvEntityDescr.properties[tileDescription.id] = VVPropertyDescription(editorId, tileDescription.id,VVPropertyType.ENTITY, "${TileData::class.qualifiedName}<${baseClassName}CompactVV, ${baseClassName}FullVV>")

                    }
                    "navigator" ->{
                        val navigatorDescription = NavigatorDescription(editorId, ParserUtils.getIdAttribute(it))
                        navigatorDescription.buttonsHandler = it.attributes["buttons-handler"]
                        layout.widgets[navigatorDescription.id] = navigatorDescription
                        it.children("variant").forEach {variantElm ->
                            val variantId = ParserUtils.getIdAttribute(variantElm)
                            val viewId = "${variantId}View"
                            navigatorDescription.viewIds.add(viewId)
                            val variantVM = VMEntityDescription("${viewId}VM")
                            variantVM.properties["caption"]= VMPropertyDescription(navigatorDescription.id, "caption",VMPropertyType.STRING, null, true)
                            val variantVS = VSEntityDescription("${viewId}VS")
                            val variantVV = VVEntityDescription("${viewId}VV")
                            registry.viewModels[variantVM.id] = variantVM
                            registry.viewSettings[variantVS.id] = variantVS
                            registry.viewValidations[variantVV.id] = variantVV
                            updateViews(registry, variantVM, variantVS, variantVV,  variantElm.children("view")[0], variantId, localizations)
                        }
                        lateinit var modelId:String
                        lateinit var settingsId:String
                        lateinit var validationId:String
                        if(navigatorDescription.viewIds.size ==1){
                            val viewDescr = registry.views[navigatorDescription.viewIds[0]]!!
                            modelId = viewDescr.viewModel
                            settingsId = viewDescr.viewSettings
                            validationId = viewDescr.viewValidation
                        } else {
                            modelId = BaseVMEntity::class.qualifiedName!!
                            settingsId = BaseVSEntity::class.qualifiedName!!
                            validationId = BaseVVEntity::class.qualifiedName!!
                        }
                        vmEntityDescr.collections[navigatorDescription.id] = VMCollectionDescription(editorId, navigatorDescription.id, VMCollectionType.ENTITY, modelId)
                        vsEntityDescr.collections[navigatorDescription.id] = VSCollectionDescription(editorId, navigatorDescription.id, VSCollectionType.ENTITY, settingsId)
                        vvEntityDescr.collections[navigatorDescription.id] = VVCollectionDescription(editorId, navigatorDescription.id, VVCollectionType.ENTITY, validationId)

                    }
                }
            }
        }

        val viewDescr = StandardViewDescription(id = "${editorId}View",
                layout = layout, viewModel = vmEntityDescr.id,  viewSettings = vsEntityDescr.id,viewValidation = vvEntityDescr.id
        )
        viewDescr.interceptors.addAll(interceptors)
        registry.views[viewDescr.id] = viewDescr
        return viewDescr
    }

    private fun updateNotEditable(tb: BaseWidgetDescription, it: XmlNode) {
        tb.notEditable = "true" == it.attributes["not-editable"]
    }


    private fun updateHspan(tb: BaseWidgetDescription, it: XmlNode) {
        tb.hSpan = it.attributes["h_span"]?.let{Integer.parseInt(it)}
    }


}
