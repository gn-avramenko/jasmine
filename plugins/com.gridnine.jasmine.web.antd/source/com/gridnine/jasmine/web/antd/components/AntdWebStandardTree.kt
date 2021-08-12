/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*

class AntdWebStandardTree(private val config: WebTreeConfiguration) : WebTree, BaseAntdWebUiComponent() {

    private var contextMenuBuilder: ((WebTreeNode) -> List<WebContextMenuItem>?)? = null

    private val data = arrayListOf<WebTreeNode>()

    private var selectionListener: (suspend (item: WebTreeNode) -> Unit)? = null

    private var dropListener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Unit)? = null

    private var selectedNodeId:String?  = null

    private val expandedNodesIds = arrayListOf<String>()

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy { callbackIndex ->
            val props = js("{}")
            props.className = "draggable-tree"
            props.style = js("{}")
            if (config.fit) {
                props.style.width = "100%"
                props.style.height = "100%"
            } else {
                config.width?.let { props.style.width = it }
                config.height?.let { props.style.height = it }
            }
            props.draggable =config.enableDnd
            props.defaultSelectedKeys = if(selectedNodeId == null) js("[]") else arrayOf(selectedNodeId)
            props.defaultExpandedKeys = expandedNodesIds.toTypedArray()
            ReactFacade.callbackRegistry.get(callbackIndex).onSelect = { selectedKeys: Array<String> ->
                if (selectedKeys.isNotEmpty()) {
                    selectionListener?.let { lst ->
                        findNode(selectedKeys[0], null, data)?.first?.let { node ->
                            launch {
                                lst.invoke(node)
                            }
                        }
                    }
                }
            }
            props.onSelect = { selectedKeys: Array<String> ->
                ReactFacade.callbackRegistry.get(callbackIndex).onSelect(selectedKeys)
            }
            ReactFacade.callbackRegistry.get(callbackIndex).onExpand = { expandedKeys: Array<String> ->
                this.expandedNodesIds.clear()
                this.expandedNodesIds.addAll(expandedKeys)
            }
            props.onExpand = { expandedKeys: Array<String> ->
                ReactFacade.callbackRegistry.get(callbackIndex).onExpand(expandedKeys)
            }
            if(config.enableDnd) {
                ReactFacade.callbackRegistry.get(callbackIndex).onDrop =
                    { event:dynamic ->
                        dropListener?.let{
                            val dragKey = event.dragNode.key as String
                            val dragNode = findNode(dragKey, null, data)?.first
                            val targetKey = event.node.key as String
                            val targetNode = findNode(targetKey, null, data)?.first
                            if(targetNode != null && dragNode!= null){
                                it.invoke(targetNode, dragNode, WebTreeInsertNodePoint.BOTTOM)
                            }
                        }

                    }
                props.onDrop = { event: dynamic ->
                    ReactFacade.callbackRegistry.get(callbackIndex).onDrop(event)
                }
            }
            ReactFacade.createElementWithChildren(
                ReactFacade.Tree,
                props,
                generateChildren(data, callbackIndex).toTypedArray()
            )
        }
    }

    private fun generateChildren(data: List<WebTreeNode>, callbackIndex: Int): List<ReactElement> {
        return data.map { node ->
            val nodeProps = js("{}")
            nodeProps.key = node.id
            val items = contextMenuBuilder?.invoke(node)?.filterIsInstance<WebContextMenuStandardItem>()
            if (items != null) {
                val menuProps = js("{}")
                val functionName = "onClick${node.id}"
                ReactFacade.callbackRegistry.get(callbackIndex)[functionName] = { event: dynamic ->
                    val key = (event.key as String).substringAfterLast("key").toInt()
                    val item = items[key]
                    launch {
                        item.handler.invoke()
                    }
                }
                menuProps.onClick = { event: dynamic ->
                    ReactFacade.callbackRegistry.get(callbackIndex)[functionName](event)
                }
                val contextMenu = ReactFacade.createElementWithChildren(
                    ReactFacade.Menu,
                    menuProps,
                    items.withIndex().map { (index, item) ->
                        val menuItemProps = js("{}")
                        menuItemProps.key = "${node.id}_key${index}"
                        ReactFacade.createElementWithChildren(ReactFacade.MenuItem, menuItemProps, item.text)
                    }.toTypedArray()
                )
                nodeProps.isLeaf = node.children.isEmpty()
                val dropdownProps = js("{}")
                dropdownProps.overlay = contextMenu
                dropdownProps.trigger = arrayOf("contextMenu")
                val dropdown = ReactFacade.createElementWithChildren(
                    ReactFacade.Dropdown,
                    dropdownProps,
                    ReactFacade.createElementWithChildren("div", js("{}"), node.text)
                )
                nodeProps.title = dropdown
            } else {
                nodeProps.title = node.text
            }
            if (node.children.isNotEmpty()) {
                ReactFacade.createElementWithChildren(
                    ReactFacade.TreeNode,
                    nodeProps,
                    generateChildren(node.children, callbackIndex).toTypedArray()
                )
            } else {
                ReactFacade.createElement(ReactFacade.TreeNode, nodeProps)
            }
        }
    }

    override fun setData(data: List<WebTreeNode>) {
        if (this.data != data) {
            this.data.clear()
            data.forEach {
                this.data.add(cloneNode(it))
            }
            maybeRedraw()
        }
    }

    private fun cloneNode(it: WebTreeNode): WebTreeNode {
        val result = WebTreeNode(it.id, it.text, it.userData)
        it.children.forEach {
            result.children.add(cloneNode(it))
        }
        return result
    }

    override fun setSelectListener(listener: (suspend (item: WebTreeNode) -> Unit)?) {
        this.selectionListener = listener
    }

    override fun setOnBeforeDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Boolean)?) {
        //noops
    }

    override fun setOnDragEnterListener(listener: ((target: WebTreeNode, source: WebTreeNode) -> Boolean)?) {
        //noops
    }

    override fun setOnDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Unit)?) {
        this.dropListener = listener
    }

    override fun setContextMenuBuilder(builder: (WebTreeNode) -> List<WebContextMenuItem>?) {
        this.contextMenuBuilder = builder
    }


    override fun findNode(id: String): WebTreeNode? {
        return findNode(id, null, data)?.first
    }

    override fun select(id: String) {
        selectedNodeId = id
        maybeRedraw()
    }

    override fun updateText(id: String, text: String) {
        val existingNode = findNode(id, null, data)?.first?:return
        existingNode.text = text
        maybeRedraw()
    }

    override fun updateUserData(id: String, data: Any?) {
        val existingNode = findNode(id, null, this.data)?.first?:return
        existingNode.userData = data
    }

    override fun getData(): List<WebTreeNode> {
        return data
    }

    override fun append(webTreeNode: WebTreeNode, parentId: String) {
        val parentNode = findNode(parentId, null, data)?.first?:return
        parentNode.children.add(webTreeNode)
        maybeRedraw()
    }

    override fun remove(id: String) {
        val node = findNode(id, null, data)?:return
        val children = if(node.second == null) data else node.second!!.children
        if(children.removeAll { it.id == id }){
            maybeRedraw()
        }
    }

    override fun insertAfter(node: WebTreeNode, targetId: String) {
        val nodes = findNode(targetId, null, data)?:return
        val children = if(nodes.second == null) data else nodes.second!!.children
        val idx = children.indexOf(nodes.first)+1
        children.add(idx, node)
        maybeRedraw()
    }

    override fun insertBefore(node: WebTreeNode, targetId: String) {
        val nodes = findNode(targetId, null, data)?:return
        val children = if(nodes.second == null) data else nodes.second!!.children
        val idx = children.indexOf(nodes.first)
        children.add(idx, node)
        maybeRedraw()
    }

    private fun findNode(
        id: String,
        parent: WebTreeNode?,
        children: List<WebTreeNode>
    ): Pair<WebTreeNode, WebTreeNode?>? {
        for (node in children) {
            if (node.id == id) {
                return node to parent
            }
            if (node.children.isNotEmpty()) {
                val cn = findNode(id, node, node.children)
                if (cn != null) {
                    return cn
                }
            }
        }
        return null
    }

}