/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")


package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.UiUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

@Suppress("UnsafeCastFromDynamic")
class EasyUiTableWidget<VM : BaseVMEntityJS, VS : BaseVSEntityJS, VV : BaseVVEntityJS>(uid: String, val description: TableDescriptionJS) : TableWidget<VM, VS, VV>() {
    private val div: JQuery = jQuery("#${description.id}${uid}")

    private var editIndex:Int? = null

    private lateinit var lastConfig:TableConfigurationJS<VS>


    private var borderColor:dynamic = null

    private var borderWidth:dynamic = null


    init {
        configure = { configuration ->
            lastConfig = configuration
            val columns = arrayListOf<dynamic>()
            description.columns.values.forEach { columnDescription ->
                val formatter: (dynamic, dynamic, dynamic) -> String? = when (columnDescription) {
                    is TextTableColumnDescriptionJS -> {
                        { value: String?, row: dynamic, _: dynamic ->
                            wrapWithSpan(row, value?.let { if (it.length > 30) it.substring(0, 30) else it }, columnDescription.id)
                        }
                    }
                    is SelectTableColumnDescriptionJS -> {
                        { value: dynamic, row: dynamic, _: dynamic ->
                            wrapWithSpan(row, (value as SelectItemJS?)?.let { it.caption?.let { caption -> if (caption.length > 30) caption.substring(0, 30) else caption } }, columnDescription.id)
                        }
                    }
                    is EnumTableColumnDescriptionJS -> {
                        { value: dynamic, row: dynamic, _: dynamic ->
                            wrapWithSpan(row, (if(value is Enum<*>) value.name else value as String?).let { DomainMetaRegistryJS.get().enums[columnDescription.enumId+"JS"]?.items?.get(it)?.displayName?.let { caption -> if (caption.length > 30) caption.substring(0, 30) else caption } }, columnDescription.id)
                        }
                    }
                    is EntityTableColumnDescriptionJS -> {
                        { value: dynamic, row: dynamic, _: dynamic ->
                            wrapWithSpan(row, (if(value is EntityReferenceJS) value.caption else value as String?)?.let {  caption -> if (caption.length > 30) caption.substring(0, 30) else caption }, columnDescription.id)
                        }
                    }
                    else -> { value: dynamic, row: dynamic, _: dynamic ->
                        wrapWithSpan(row, (value as Any?)?.let { it.toString()?.let { caption -> if (caption.length > 30) caption.substring(0, 30) else caption } }, columnDescription.id)
                    }
                }
                columns.add(object {
                    val field = columnDescription.id
                    val title = columnDescription.displayName
                    val formatter = formatter
                    val width = 100
                    val editor = if (!configuration.nonEditable) createEditor(columnDescription, configuration.columnSettings.getValue(columnDescription.id)) else null
                })
            }
            if(!configuration.nonEditable){
                columns.add(object {
                    val field = "controlButtons"
                    val title = "<div style=\"display:inline-block;width:15px;height:30px;float:right\" id=\"${description.id}${uid}AddToolButton\" class = \"jasmine-datagrid-expand\"></div>"
                    val formatter = { _: dynamic, row: dynamic, _: dynamic ->
                        "<div style=\"display:inline-block;width:15px;height:30px\" id=\"${row.uid}Up\" class = \"jasmine-datagrid-sort-asc\"></div>"+
                                "<div style=\"display:inline-block;width:15px;height:30px\" id=\"${row.uid}Down\" class = \"jasmine-datagrid-sort-desc\"></div>"+
                        "<div style=\"display:inline-block;width:15px;height:30px\" id=\"${row.uid}Add\" class = \"jasmine-datagrid-expand\"></div>"+
                                "<div style=\"display:inline-block;width:15px;height:30px\" id=\"${row.uid}Remove\" class = \"jasmine-datagrid-collapse\"></div>"
                    }
                    val fixed = true
                    val width = 70
                })

            }
            div.datagrid(object {
                val fitColumns = true
                val columns = arrayOf(columns.toTypedArray())
                val singleSelect = true
                val onClickCell =  if (!configuration.nonEditable) this@EasyUiTableWidget::createOnClickCell else null
                val onEndEdit = if (!configuration.nonEditable) this@EasyUiTableWidget::createOnEndEdit else null
            })
            if(!configuration.nonEditable){
                val addButtonDiv = jQuery("#${description.id}${uid}AddToolButton").asDynamic()
                addButtonDiv.off("click")
                addButtonDiv.click{
                    div.datagrid("insertRow", object{
                        val index = 0
                        val row = object{
                            val uid = TextUtilsJS.createUUID()
                        }
                    })
                    bindControls()
                    div.datagrid("selectRow", 0)
                }
            }
        }

        writeData = {
            endEditing()
            it.clear()
            val tableData = div.datagrid("getData").asDynamic().rows
            tableData.forEach{ row:dynamic ->
                val vmClassName = "${description.className}VMJS"
                val modelRow = ReflectionFactoryJS.get().getFactory(vmClassName).invoke() as VM
                modelRow.setValue(BaseEntityJS.uid, row[BaseEntityJS.uid])
                description.columns.values.forEach {columnDescr ->
                    modelRow.setValue(columnDescr.id, when(columnDescr){
                        is SelectTableColumnDescriptionJS ->{
                            val tableValue = row[columnDescr.id]
                            var modelValue:SelectItemJS? = null
                            if(tableValue != null){
                                modelValue = SelectItemJS(tableValue["id"], tableValue["caption"])
                            }
                            modelValue
                        }
                        else ->{
                            row[columnDescr.id]
                        }
                    })
                }
                it.add(modelRow)
                Unit
            }

        }

        readData = {
           div.datagrid("loadData", it.toTypedArray())
            if(!lastConfig.nonEditable){
                bindControls()
            }

        }

        showValidation = {
            (div.datagrid("getRows") as Array<dynamic>).toList().withIndex().forEach { (idx, item) ->
               description.columns.values.forEach { columnDescr ->
                   val validation = it[idx].getValue(columnDescr.id)
                   val tdElm = jQuery("#cell-wrapper-${columnDescr.id}-${item.uid}").asDynamic().closest("td")
                   if(borderColor == null){
                       borderColor = tdElm.css("border-color")
                       borderWidth =  tdElm.css("border-width")
                   }
                   if (validation == null) {
                       tdElm.css("border-color", borderColor)
                       tdElm.css("border-width", borderWidth)
                       tdElm.removeAttr("title")
                   } else  {
                       tdElm.css("border-color", "#d9534f")
                       tdElm.css("border-width", "2px")
                       tdElm.attr("title", validation)
                   }
               }
            }
        }
    }

    private fun wrapWithSpan(row:dynamic, content: String?, fieldId:String): String {
        return "<span id=\"cell-wrapper-${fieldId}-${row.uid}\">${content?:""}</span>"
    }

    private fun bindControls() {
        div.datagrid("getData").asDynamic().rows.forEach { rowVM ->
            bindControl(rowVM.uid, "Up") { idx,_->
                if(idx > 0){
                    moveRow(idx,idx-1)
                }
            }
            bindControl(rowVM.uid, "Down") { idx,totalRowsCount->
                if(idx < totalRowsCount-1){
                    moveRow(idx,idx+1)
                }
            }
            bindControl(rowVM.uid, "Add") { idx,_->
                div.datagrid("insertRow", object{
                    val index = idx+1
                    val row = object{
                        val uid = TextUtilsJS.createUUID()
                    }
                })
                bindControls()
                div.datagrid("selectRow", idx+1)
            }
            bindControl(rowVM.uid, "Remove") { idx,_->
                div.datagrid("deleteRow", idx)
                bindControls()
            }
        }
    }

    private fun moveRow(fromIdx: Int, toIdx: Int) {
        val rows = div.datagrid("getRows").asDynamic()
        val fromRow = rows[fromIdx]
        val toRow = rows[toIdx]
        val toIndex = div.datagrid("getRowIndex", toRow)
        div.datagrid("deleteRow", fromIdx)
        div.datagrid("insertRow", object{
            val index = toIndex
            val row = fromRow
        })
        bindControls()
        div.datagrid("selectRow", toIdx)
    }

    private fun bindControl(rowUid:String, suffix:String, handler:(rowIdx:Int, totalRowCount:Int)->Unit){
        val icon = jQuery("#${rowUid}$suffix").asDynamic()
        icon.off("click")
        icon.click{
            endEditing()
            val rows = div.datagrid("getData").asDynamic().rows
            var fromIdx = 0
            (rows as Array<dynamic>).withIndex().forEach lab@{(idx, row) ->
                if(row.uid == rowUid){
                    fromIdx = idx
                    return@lab
                }
            }
            handler.invoke(fromIdx, rows.size)
        }
    }

    private fun createEditor(columnDescription: BaseTableColumnDescriptionJS, value: Any?): dynamic {
        return when (columnDescription) {
            is SelectTableColumnDescriptionJS -> {
                val config = value as SelectColumnConfigurationJS
                val selectItems = arrayListOf<SelectItemJS?>()
                selectItems.clear()
                selectItems.addAll(config.possibleValues)
                selectItems.sortBy { it?.caption }
                if (config.nullAllowed) {
                    selectItems.add(0, SelectItemJS(null,null))
                }
                object {
                    val type = "combobox"
                    val options = object {
                        val valueField = "id"
                        val textField = "caption"
                        val limitToList = true
                        val data = selectItems.toTypedArray()
                    }
                }
            }
            is EnumTableColumnDescriptionJS -> {
                val selectItems = arrayListOf<SelectItemJS?>()
                selectItems.clear()
                selectItems.addAll(UiUtilsJS.getEnumValues(columnDescription.enumId+"JS"))
                selectItems.sortBy { it?.caption }
                selectItems.add(0, SelectItemJS(null,null))
                object {
                    val type = "combobox"
                    val options = object {
                        val valueField = "id"
                        val textField = "caption"
                        val limitToList = true
                        val data = selectItems.toTypedArray()
                    }
                }
            }
            is EntityTableColumnDescriptionJS -> {
                val config = value as EntityTableColumnDescriptionJS
                val options = object {
                    private var selectedValue:SelectItemJS? = null
                    private var ignoreSearchRequest = false
                    val valueField = "id"
                    val textField = "caption"
                    val editable = true
                    val limitToList = true
                    val hasDownArrow =  true
                    val multiple = false
                    val mode = "remote"
                    val onChange = { newValue: String, _: String? ->
                        selectedValue = if(newValue.isNotBlank()) toSelectItem(toReference(newValue)) else null

                        div.combobox("getIcon",0).asDynamic().css("visibility",if(selectedValue == null) "hidden" else "visible")
                    }
                    val loader = loader@ {	param:dynamic,success:dynamic,_:dynamic ->
                        if(ignoreSearchRequest){
                            selectedValue?.let { success(arrayOf(it))}?:success(emptyArray<SelectItemJS>())
                            return@loader true
                        }

                        val request = EntityAutocompleteRequestJS()
                        request.limit =10
                        request.searchText = param.q
                        DomainMetaRegistryJS.get().indexes.values.filter { it.document == config.entityClassName }.forEach { request.entitiesIds.add(it.id) }
                        StandardRestClient.standard_standard_defaultAutocomplete(request).then { response ->
                            val set = response.items.map { toSelectItem(it) }.toMutableSet()
                            selectedValue?.let { set.add(it) }
                            val result = set.toMutableList().sortedBy { it.caption }
                            success(result.toTypedArray())
                        }
                        return@loader true
                    }
                    val icons = arrayOf(object{
                        val iconCls = "icon-clear"
                        val handler = {_:dynamic ->
                            ignoreSearchRequest = true
                            selectedValue = null
                            div.combobox("setValues", arrayOfNulls<String>(0))
                            ignoreSearchRequest = false
                        }
                    })
                }
                object {
                    val type = "combobox"
                    val options = object {
                        val valueField = "id"
                        val textField = "caption"
                        val limitToList = true
                        val data = selectItems.toTypedArray()
                    }
                }
            }
            is TextTableColumnDescriptionJS -> {
                object {
                    val type = "textbox"
                }
            }
            is FloatTableColumnDescriptionJS -> {
                object {
                    val type = "numberbox"
                }
            }
            is IntegerTableColumnDescriptionJS -> {
                object {
                    val type = "numberbox"
                    val precision = 0
                }
            }
            else -> null
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createOnEndEdit(index:Int, row:dynamic, changes:dynamic) {
        //console.log(row)
        description.columns.values.forEach {
            when(it){
                is SelectTableColumnDescriptionJS ->{
                    val value = row[it.id]
                    if(value is String  && (value as String).isNotBlank()){
                        val columnConfig = lastConfig.columnSettings.getValue(it.id) as SelectColumnConfigurationJS
                        row[it.id] = columnConfig.possibleValues.find { valIt -> valIt.id == value }
                    } else {
                        row[it.id] = null
                    }
                }
//                is EnumTableColumnDescriptionJS ->{
//                    val value = row[it.id]
//                    if(value is String && (value as String).isNotBlank()){
//                        row[it.id] = ReflectionFactoryJS.get().getEnum<FakeEnumJS>(it.enumId+"JS", value)
//                    } else {
//                        row[it.id] = null
//                    }
//                }
                else -> {}
            }

        }

    }

    private fun createOnClickCell(index: Int, field: dynamic) {
        if (editIndex == index) {
            return
        }
        endEditing()
        div.datagrid("selectRow", index)
        div.datagrid("beginEdit", index)
        val ed = div.datagrid("getEditor", object {
            val index = index
            val field = field
        }).asDynamic()
        if (ed != null) {
            val target = jQuery(ed.target)
            if(target.data("textbox") != null){
                target.textbox("textbox").asDynamic().focus()
            } else {
                target.focus()
            }
        }
        editIndex = index
    }

    private fun endEditing(){
        editIndex?.let{
            div.datagrid("endEdit", editIndex)
            editIndex = null
        }
    }

}

