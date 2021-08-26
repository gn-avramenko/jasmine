/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*

class AntdWebNavigationTree (private val config:WebTreeConfiguration): WebTree,BaseAntdWebUiComponent(){

    private val data = arrayListOf<WebTreeNode>()

    private var selectedKey:String? = null

    private var listener: (suspend (item: WebTreeNode) -> Unit)? = null

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxy(parentIndex){parentIndexValue:Int?, childIndex:Int ->
            val menuProps = js("{}")
            menuProps.mode = "inline"
            val menuStyle = js("{}")
            menuProps.style = menuStyle
            if(config.fit){
                menuStyle.width = "100%"
                menuStyle.height = "100%"
            } else {
                if(config.width != null){
                    menuStyle.width = config.width
                }
                if(config.height != null){
                    menuStyle.height = config.height
                }
            }
            menuProps.onClick = { event:dynamic ->
                val key = event.key as String
                val pair = findNode(key, null, data)
                if(pair?.second != null){
                    if(listener != null){
                        launch {
                            listener!!.invoke(pair.first)
                        }
                    }
                }
            }
            ReactFacade.createElementWithChildren(ReactFacade.Menu, menuProps, data.map { createSubMenu(it) }.toTypedArray())
        }
    }

    private fun createSubMenu(node:WebTreeNode): ReactElement {
        val iconProps = js("{}")
        iconProps.className = "jasmine-submenu"
        return ReactFacade.createElementWithChildren(ReactFacade.SubMenu, object{
            val key = node.id
            val title = node.text
            val icon = ReactFacade.createElement(ReactFacade.IconFolderOutlined, iconProps)
        }, node.children.map { createMenuItem(it) }.toTypedArray())
    }

    private fun createMenuItem(node: WebTreeNode):ReactElement {
        return ReactFacade.createElementWithChildren(ReactFacade.MenuItem, object{
            val key = node.id
        }, node.text)
    }

    override fun setData(data: List<WebTreeNode>) {
        this.data.clear()
        this.data.addAll(data)
        maybeRedraw()
    }


    override fun setSelectListener(listener: (suspend (item: WebTreeNode) -> Unit)?) {
        this.listener = listener
    }

    override fun setOnBeforeDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Boolean)?) {
        throw XeptionJS.forDeveloper("not implemented for NAVIGATION mold")
    }

    override fun setOnDragEnterListener(listener: ((target: WebTreeNode, source: WebTreeNode) -> Boolean)?) {
        throw XeptionJS.forDeveloper("not implemented for NAVIGATION mold")
    }

    override fun setOnDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Unit)?) {
        throw XeptionJS.forDeveloper("not implemented for NAVIGATION mold")
    }

    override fun setContextMenuBuilder(builder: (WebTreeNode) -> List<WebContextMenuItem>?) {
        throw XeptionJS.forDeveloper("not implemented for NAVIGATION mold")
    }

    override fun findNode(id: String): WebTreeNode? {
        return findNode(id, null, data)?.first

    }

    private fun findNode(id:String, parent:WebTreeNode?, children:List<WebTreeNode>):Pair<WebTreeNode, WebTreeNode?>?{
        for(node in children){
            if(node.id == id){
                return node to parent
            }
            if(node.children.isNotEmpty()){
                val cn = findNode(id, node, node.children)
                if(cn != null){
                    return cn
                }
            }
        }
        return null
    }

    override fun select(id: String) {
        if(id != selectedKey){
            selectedKey = id
            maybeRedraw()
        }
    }

    override fun updateText(id: String, text: String) {
        val node = findNode(id)?:return
        if(node.text != text){
            node.text = text
            maybeRedraw()
        }
    }

    override fun updateUserData(id: String, data: Any?) {
        val node = findNode(id)?:return
        node.userData = data
    }

    override fun getData(): List<WebTreeNode> {
        return data
    }

    override fun append(webTreeNode: WebTreeNode, parentId: String) {
        val parent = findNode(parentId)?:return
        parent.children.add(webTreeNode)
        maybeRedraw()
    }

    override fun remove(id: String) {
        val pair = findNode(id, null, data)?:return
        val collection = pair.second?.children?:data
        collection.remove(pair.first)
        maybeRedraw()
    }

    override fun insertAfter(node: WebTreeNode, targetId: String) {
        val pair = findNode(targetId, null, data)?:return
        val collection = pair.second?.children?:data
        val idx = collection.indexOf(pair.first)
        collection.add(idx+1, pair.first)
        maybeRedraw()
    }

    override fun insertBefore(node: WebTreeNode, targetId: String) {
        val pair = findNode(targetId, null, data)?:return
        val collection = pair.second?.children?:data
        val idx = collection.indexOf(pair.first)
        collection.add(idx, pair.first)
        maybeRedraw()
    }
}