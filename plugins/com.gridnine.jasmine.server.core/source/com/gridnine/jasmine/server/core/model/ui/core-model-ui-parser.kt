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
            dialog.closable = "true" == child.attributes["closable"]
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

    private fun updateViews(registry: UiMetaRegistry, vmEntityDescr: VMEntityDescription, vsEntityDescr: VSEntityDescription,
                            vvEntityDescr: VVEntityDescription,  viewElm: XmlNode, editorId: String, localizations: Map<String, Map<Locale, String>>) {
        lateinit var layout:BaseLayoutDescription
        val additionalData = viewElm.attributes["additionalDataClass"]
        if(additionalData != null){
            vmEntityDescr.properties["additionalData"] = VMPropertyDescription(vmEntityDescr.id, "additionalData", VMPropertyType.ENTITY, additionalData, true)
        }
        viewElm.children.forEach {layoutNode ->
            if(layoutNode.name == "table-layout"){
                layout = TableLayoutDescription(layoutNode.attributes["expandLastRow"] == "true")
                val tableLayout = layout as TableLayoutDescription
                layoutNode.children("column").forEach {
                    tableLayout.columns.add(TableColumnDescription(it.attributes["width"]))
                }
            }
            layoutNode.children.forEach {
                val viewPropertyNotNullable = "true" == it.attributes["notNullable"]
                when(it.name){
                    "text-box" ->{
                        val tb = TextboxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(tb, it)
                        layout.widgets[tb.id] = tb
                        vmEntityDescr.properties[tb.id] = VMPropertyDescription(editorId, tb.id, VMPropertyType.STRING, null, false)
                        vvEntityDescr.properties[tb.id] = VVPropertyDescription(editorId, tb.id, VVPropertyType.STRING, null)
                    }
                    "password-box" ->{
                        val tb = PasswordBoxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(tb, it)
                        layout.widgets[tb.id] = tb
                        vmEntityDescr.properties[tb.id] = VMPropertyDescription(editorId, tb.id, VMPropertyType.STRING, null, false)
                        vvEntityDescr.properties[tb.id] = VVPropertyDescription(editorId, tb.id, VVPropertyType.STRING, null)
                    }
                    "enum-select" ->{
                        val es = EnumSelectDescription(editorId, ParserUtils.getIdAttribute(it), it.attributes["enum-id"]?:throw IllegalArgumentException("${it.name} has no enum-id attribute"))
                        updateHspan(es, it)
                        layout.widgets[es.id] = es
                        val vmp = VMPropertyDescription(editorId, es.id, VMPropertyType.ENUM, es.enumId, false)
                        vmEntityDescr.properties[es.id] = vmp
                        vsEntityDescr.properties[es.id] = VSPropertyDescription(editorId, es.id, VSPropertyType.ENUM_SELECT, es.enumId)
                        vvEntityDescr.properties[es.id] = VVPropertyDescription(editorId, es.id, VVPropertyType.STRING, null)
                    }
                    "select" ->{
                        val es = SelectDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(es, it)
                        layout.widgets[es.id] = es
                        val vmp = VMPropertyDescription(editorId, es.id, VMPropertyType.SELECT, null, false)
                        vmEntityDescr.properties[es.id] = vmp
                        vsEntityDescr.properties[es.id] = VSPropertyDescription(editorId, es.id, VSPropertyType.SELECT, null)
                        vvEntityDescr.properties[es.id] = VVPropertyDescription(editorId, es.id, VVPropertyType.STRING, null)
                    }
                    "entity-select" ->{
                        val es = EntitySelectDescription(editorId, ParserUtils.getIdAttribute(it), it.attributes["entity-class-name"]?:throw IllegalArgumentException("${it.name} has no entity-class-name attribute"))
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
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.STRING, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "integer-box" ->{
                        val ta = IntegerBoxDescription(editorId, ParserUtils.getIdAttribute(it), "true" == it.attributes["notNullable"])
                        updateHspan(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.INT, null,viewPropertyNotNullable)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "float-box" ->{
                        val ta = FloatBoxDescription(editorId, ParserUtils.getIdAttribute(it), "true" == it.attributes["notNullable"])
                        updateHspan(ta, it)
                        ParserUtils.updateLocalizations(ta, localizations)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.BIG_DECIMAL, null,viewPropertyNotNullable)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "date-box" ->{
                        val ta = DateboxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.LOCAL_DATE, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "date-time-box" ->{
                        val ta = DateTimeBoxDescription(editorId, ParserUtils.getIdAttribute(it))
                        updateHspan(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.LOCAL_DATE, null,false)
                        vvEntityDescr.properties[ta.id] = VVPropertyDescription(editorId, ta.id, VVPropertyType.STRING, null)
                    }
                    "boolean-box" ->{
                        val ta = BooleanBoxDescription(editorId, ParserUtils.getIdAttribute(it), "true" == it.attributes["notNullable"])
                        updateHspan(ta, it)
                        layout.widgets[ta.id] = ta
                        vmEntityDescr.properties[ta.id] = VMPropertyDescription(editorId, ta.id, VMPropertyType.BOOLEAN, null,viewPropertyNotNullable)
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
                        val vAlign = it.attributes["v_align"]?.let {attr -> VerticalAlignment.valueOf(attr)}
                        val hAlign = it.attributes["h_align"]?.let {attr -> HorizontalAlignment.valueOf(attr)}
                        val ta = LabelDescription(editorId, ParserUtils.getIdAttribute(it), vAlign, hAlign)
                        updateHspan(ta, it)
                        ParserUtils.updateLocalizations(ta, localizations, ParserUtils.getCaptionAttribute(it))
                        layout.widgets[ta.id] = ta
                    }
                    "table" ->{
                        val baseClassName = it.attributes["class-name"]?:throw IllegalArgumentException("${it.name} has no class-name attribute")
                        val tableDescription = TableDescription(editorId, ParserUtils.getIdAttribute(it),baseClassName)
                        tableDescription.additionalRowDataClass =it.attributes["additional-row-data-class"]
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
                            val columnPropertyNotNullable = "true" == columnNode.attributes["notNullable"]

                            when (columnNode.name) {
                                "text-column" -> {
                                    val columnDescription = TextTableColumnDescription(tableDescription.fullId, id)
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    columnDescription.width = width
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.STRING, null,false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_TEXT, null)
                                }
                                "integer-column" -> {
                                    val columnDescription = IntegerTableColumnDescription(tableDescription.fullId, id, columnPropertyNotNullable)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.INT, null,columnPropertyNotNullable)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_INT, null)
                                }
                                "float-column" -> {
                                    val columnDescription = FloatTableColumnDescription(tableDescription.fullId, id, columnPropertyNotNullable)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.BIG_DECIMAL, null, columnPropertyNotNullable )
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_FLOAT, null)
                                }
                                "enum-column" -> {
                                    val columnDescription = EnumTableColumnDescription(tableDescription.fullId, id, columnNode.attributes["enum-id"]
                                            ?:throw IllegalArgumentException("${columnNode.name} has no enum-id attribute"))
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.ENUM, columnDescription.enumId, false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_ENUM_SELECT, columnDescription.enumId)
                                }
                                "entity-column" -> {
                                    val columnDescription = EntityTableColumnDescription(tableDescription.fullId, id, columnNode.attributes["entity-class-name"]
                                            ?:throw IllegalArgumentException("${columnNode.name} has no entity-class-name attribute"))
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.ENTITY_REFERENCE, columnDescription.entityClassName,false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_ENTITY, columnDescription.entityClassName)
                                }
                                "date-column" -> {
                                    val columnDescription = DateTableColumnDescription(tableDescription.fullId, id)
                                    columnDescription.width = width
                                    ParserUtils.updateLocalizations(columnDescription, localizations)
                                    tableDescription.columns[id] = columnDescription
                                    tableVM.properties[id] = VMPropertyDescription(tableDescription.fullId,id,VMPropertyType.LOCAL_DATE, null, false)
                                    tableVV.properties[id] = VVPropertyDescription(tableDescription.fullId,id,VVPropertyType.STRING, null)
                                    tableVS.properties[id] = VSPropertyDescription(tableDescription.fullId,id,VSPropertyType.COLUMN_DATE, null)
                                }
                            }
                        }
                    }
                }
            }
        }

        val viewDescr = StandardViewDescription(id = "${editorId}View",
                layout = layout, viewModel = vmEntityDescr.id,  viewSettings = vsEntityDescr.id,viewValidation = vvEntityDescr.id
        )
        registry.views[viewDescr.id] = viewDescr
    }



    private fun updateHspan(tb: BaseWidgetDescription, it: XmlNode) {
        tb.hSpan = it.attributes["h_span"]?.let{Integer.parseInt(it)}
    }


}