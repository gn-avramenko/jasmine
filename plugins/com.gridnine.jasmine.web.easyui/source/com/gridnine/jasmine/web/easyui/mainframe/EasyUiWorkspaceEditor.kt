/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UNUSED_VARIABLE")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceDTJS
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceGroupDTJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.serialization.CloneUtilsJS
import com.gridnine.jasmine.web.core.ui.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.showError
import kotlin.js.Promise

@Suppress("UnsafeCastFromDynamic")
class EasyUiWorkspaceEditor : EasyUiTabHandler<WorkspaceDTJS>{

    private var lastEditor:EasyUiWorkspaceElementEditor<Any>? = null

    private var lastNode:dynamic = null

    override fun getId(): String {
        return "workspace-editor"
    }

    override fun getData(uid: String): Promise<WorkspaceDTJS> {
        return Promise { resolve, _ ->
            StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS()).then { resolve(it.workspace) }
        }
    }

    override fun getTitle(data: WorkspaceDTJS): String {
        return "Рабочая область"
    }

    override fun getContent(data: WorkspaceDTJS, uid: String): String {
        return HtmlUtilsJS.html {
            div(id = "workspace-editor", `class` = "easyui-layout", data_options = "fit:true") {
                div(data_options = "region:'west',split:true,width:200") {
                    ul(id = "workspace-editor-tree") {}
                    div (id="workspace-editor-tree-group-menu",style = "display:none") {
                        div(id="workspace-editor-tree-group-menu-add-above") { "Добавить группу выше"() }
                        div(id="workspace-editor-tree-group-menu-add-below") { "Добавить группу ниже"() }
                        div(`class`="menu-sep"){}
                        div(id="workspace-editor-tree-group-menu-add-list") { "Добавить список"() }
                        div(`class`="menu-sep"){}
                        div(id="workspace-editor-tree-group-delete") { "Удалить группу"() }
                    }
                    div (id="workspace-editor-tree-item-menu",style = "display:none") {
                        div(id="workspace-editor-tree-item-menu-add") { "Добавить список"() }
                        div(`class`="menu-sep"){}
                        div(id="workspace-editor-tree-item-menu-copy") { "Копировать элемент"() }
                        div(`class`="menu-sep"){}
                        div(id="workspace-editor-tree-item-delete") { "Удалить список"() }
                    }
                }
                div( data_options = "region:'center'") {
                    div(id = "workspace-editor-center-panel", `class` = "easyui-layout", data_options = "fit:true") {
                        div (id="workspace-editor-buttons-panel", region = "north",border = false, style = "padding:5px;height:50px"){
                            a(id="workspace-editor-save-button",href = "#"){"Сохранить"()}
                        }
                        div (id="workspace-editor-element-panel",region="center", border = false, style = "padding:5px" ) { }
                    }
                }
            }
        }.toString()
    }

    override fun decorateData(data: WorkspaceDTJS, uid: String, setTitle: (String) -> Unit, close: () -> Unit) {
        jQuery("#workspace-editor").layout()
        jQuery("#workspace-editor-center-panel").layout()
        jQuery("#workspace-editor-save-button").linkbutton(object{
            val onClick = {
                console.log("clicked")
            }
        })


        val elementEditorPanel = jQuery("#workspace-editor-element-panel")
        val treeElm = jQuery("#workspace-editor-tree")
        treeElm.tree(object{
            val dnd = true
            val fit = true
            val onSelect = {node:dynamic ->
                if (lastEditor != null) {
                    val existingNode = treeElm.tree("find", lastNode.id)
                    if(existingNode != null) {
                        lastNode.userData = lastEditor!!.getData()
                        lastNode.text = lastNode.userData.displayName
                        treeElm.tree("update", object {
                            val target = lastNode.target
                            val text = lastNode.text
                        })
                    }
                }
                val userData = node.userData
                val editor =  when (userData){
                    is WorkspaceGroupDTJS -> EasyUiWorkspaceGroupEditor()
                    is ListWorkspaceItemDTJS ->EasyUiWorkspaceListEditor()
                    else -> throw IllegalArgumentException("unsupported node type $userData")
                }
                elementEditorPanel.empty()
                elementEditorPanel.html(editor.getContent())
                editor.decorate()
                editor.setData(userData)
                lastEditor = editor as EasyUiWorkspaceElementEditor<Any>
                lastNode = node
            }
            val onContextMenu = {e:dynamic, node:dynamic ->
                e.preventDefault()
                // select the node
                treeElm.tree("select", node.target)
                val menuId = when (val userData = node.userData){
                    is WorkspaceGroupDTJS -> {
                        val menuId = "workspace-editor-tree-group-menu"

                        jQuery("#$menuId").menu(object {
                            val onClick = { item:dynamic ->
                                when(item.id){
                                    "workspace-editor-tree-group-menu-add-above" ->{
                                        val elementId =TextUtilsJS.createUUID()
                                        val userDataVal = WorkspaceGroupDTJS()
                                        userDataVal.displayName = "Новая группа"
                                        treeElm.tree("insert", object{
                                            val before = node.target
                                            val data =object {
                                                val id =elementId
                                                val text = userDataVal.displayName
                                                val userData = userDataVal
                                            }
                                        })
                                        selectElement(treeElm, elementId)
                                    }
                                    "workspace-editor-tree-group-menu-add-below" ->{
                                        val elementId =TextUtilsJS.createUUID()
                                        val userDataVal = WorkspaceGroupDTJS()
                                        userDataVal.displayName = "Новая группа"
                                        treeElm.tree("insert", object{
                                            val after = node.target
                                            val data =object {
                                                val id =elementId
                                                val text = userDataVal.displayName
                                                val userData = userDataVal
                                            }
                                        })
                                        selectElement(treeElm, elementId)
                                    }
                                    "workspace-editor-tree-group-menu-add-list" ->{
                                        val elementId =TextUtilsJS.createUUID()
                                        val userDataVal = ListWorkspaceItemDTJS()
                                        userDataVal.displayName = "Новый список"
                                        treeElm.tree("append", object{
                                            val parent = node.target
                                            val data = arrayOf(object {
                                                val id =elementId
                                                val text = userDataVal.displayName
                                                val userData = userDataVal
                                            })
                                        })
                                        selectElement(treeElm, elementId)
                                    }
                                    "workspace-editor-tree-group-delete" ->{
                                        val selectedNodeId = node.id as String
                                        val roots = treeElm.tree("getRoots",Unit)
                                        if(roots.length== 1){
                                            showError(message = "Нельзя удалить единственный элемент",title = "Ошибка", stacktrace = null)
                                        } else {
                                            var previousElementId: String? = null
                                            for (group in roots) {
                                                if (group.id == selectedNodeId) {
                                                    break
                                                }
                                                previousElementId = group.id
                                            }
                                            treeElm.tree("remove", node.target)
                                            selectElement(treeElm, previousElementId?: treeElm.tree("getRoots", Unit)[0].id)
                                        }
                                    }
                                    else ->{}
                                }

                            }
                        })
                        menuId
                    }
                    is ListWorkspaceItemDTJS -> {
                        val menuId = "workspace-editor-tree-item-menu"
                        jQuery("#$menuId").menu(object {
                            val onClick = { item:dynamic ->
                                when(item.id){

                                    "workspace-editor-tree-item-menu-add" ->{
                                        val elementId =TextUtilsJS.createUUID()
                                        val userDataVal = ListWorkspaceItemDTJS()
                                        userDataVal.displayName = "Новый список"
                                        treeElm.tree("insert", object{
                                            val after = node.target
                                            val data =object {
                                                val id =elementId
                                                val text = userDataVal.displayName
                                                val userData = userDataVal
                                            }
                                        })
                                        selectElement(treeElm, elementId)
                                    }
                                    "workspace-editor-tree-item-menu-copy" ->{
                                        val elementId =TextUtilsJS.createUUID()
                                        val newUserData = CloneUtilsJS.clone(node.userData, true)
                                        newUserData.displayName += "(копия)"
                                        treeElm.tree("insert", object{
                                            val after = node.target
                                            val data =object {
                                                val id =elementId
                                                val text = newUserData.displayName
                                                val userData = newUserData
                                            }
                                        })
                                        selectElement(treeElm, elementId)
                                    }
                                    "workspace-editor-tree-item-delete" ->{
                                        val parentNode = treeElm.tree("getParent",node.target)
                                        treeElm.tree("remove", node.target)
                                        selectElement(treeElm, parentNode.id)
                                    }
                                    else ->{}
                                }

                            }
                        })

                        menuId
                    }
                    else -> throw IllegalArgumentException("unsupported element type $userData")
                }

                // display context menu
                jQuery("#$menuId").menu("show", object{
                    val left = e.pageX
                    val top =e.pageY
                })
            }

            val onDragEnter = {target:dynamic,source:dynamic ->
                this.onBeforeDrop(target,source,"append")
            }


            val onBeforeDrop =  {target:dynamic,source:dynamic,point:dynamic ->
                var result = true
                val targetNode = treeElm.tree("getNode", target)
                if(source.userData is WorkspaceGroupDTJS){
                    if(targetNode == null || targetNode.userData !is WorkspaceGroupDTJS){
                       result =  false
                    }
                } else if(source.userData is ListWorkspaceItemDTJS){
                    if(targetNode == null || (targetNode.userData is WorkspaceGroupDTJS && (point != "append")) ){
                        result =  false
                    }
                }
                result
            }

            val onDrop = { target: dynamic, source: dynamic, point: dynamic ->
                val userData = source.userData
                val si = source.id
                run {
                    val targetNode = treeElm.tree("getNode", target)
                    if (userData is ListWorkspaceItemDTJS && targetNode.userData is WorkspaceGroupDTJS) {
                        treeElm.tree("append", object {
                            val parent = target
                            val data = arrayOf(object {
                                val id = TextUtilsJS.createUUID()
                                val text = source.text
                                val userData = userData
                            })
                        })
                        return@run
                    }
                    if (point == "bottom" || point == "append") {
                        treeElm.tree("insert", object {
                            val after = target
                            val data = object {
                                val id = TextUtilsJS.createUUID()
                                val text = source.text
                                val userData = source.userData
                                val children = source.children
                            }
                        })
                        return@run
                    }
                    treeElm.tree("insert", object {
                        val before = target
                        val data = object {
                            val id = TextUtilsJS.createUUID()
                            val text = source.text
                            val userData = source.userData
                            val children = source.children
                        }
                    })
                }
                val node = treeElm.tree("find", si)
                treeElm.tree("remove", node.target)
            }

            private fun selectElement(treeElm: JQuery, elementId: String) {
                 val node = treeElm.tree("find", elementId)
                if(node != null) {
                    treeElm.tree("select", node.target)
                }
                //console.log(treeElm.tree("getRoots",Unit));
            }
        })
        val treeData = arrayOfNulls<Any>(data.groups.size)
        data.groups.withIndex().forEach {(idx, groupDT) ->
            val treeObj = js("{}")
            treeObj.id = TextUtilsJS.createUUID()
            treeObj.text = groupDT.displayName
            treeObj.state = "open"
            treeObj.userData = groupDT
            val children = arrayOfNulls<Any>(groupDT.items.size)
            groupDT.items.withIndex().forEach { (idx2, itemDT) ->
                val childObj = js("{}")
                childObj.id = TextUtilsJS.createUUID()
                childObj.text = itemDT.displayName
                childObj.userData = itemDT
                children[idx2] = childObj
            }
            treeObj.children = children
            treeData[idx] = treeObj
        }
        treeElm.tree("loadData", treeData)

    }

}

interface EasyUiWorkspaceElementEditor<T:Any> {
    fun getContent():String
    fun decorate()
    fun setData(data:T)
    fun getData():T
    fun validate():Boolean
}