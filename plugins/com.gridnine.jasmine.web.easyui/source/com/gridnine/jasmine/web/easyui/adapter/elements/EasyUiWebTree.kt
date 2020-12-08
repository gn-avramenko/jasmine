/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTree(private val parent: WebComponent?, configure: WebTreeConfiguration.() -> Unit) : WebTree {

    private val config = WebTreeConfiguration()
    private val uid = MiscUtilsJS.createUUID()
    private var initialized = false
    private var treeJq: dynamic = null
    private val preloadedData = arrayListOf<WebTreeNode>()
    private var selectionListener: ((item: WebTreeNode) -> Unit)? = null
    private var onBeforeDropListener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Boolean)? = null
    private var onDropListener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Unit)? = null
    private var onDragEnterListener: ((target: WebTreeNode, source: WebTreeNode) -> Boolean)? = null

    init {
        config.configure()
    }

    override fun setData(data: List<WebTreeNode>) {
        if (!initialized) {
            this.preloadedData.clear()
            this.preloadedData.addAll(data)
        }
        if (initialized) {
            treeJq.tree("loadData", data.map {
                toTreeItem(it)
            }.toTypedArray())
        }
    }

    override fun setSelectListener(listener: ((item: WebTreeNode) -> Unit)?) {
        selectionListener = listener
    }

    override fun setOnBeforeDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Boolean)?) {
        onBeforeDropListener = listener
    }

    override fun setOnDragEnterListener(listener: ((target: WebTreeNode, source: WebTreeNode) -> Boolean)?) {
        onDragEnterListener = listener
    }

    override fun setOnDropListener(listener: ((target: WebTreeNode, source: WebTreeNode, point: WebTreeInsertNodePoint) -> Unit)?) {
        onDropListener = listener
    }

    override fun findNode(id: String): WebTreeNode? {
        return findNodeInternal(id)?.first
    }

    private fun findNodeInternal(id:String):Pair<WebTreeNode?, WebTreeNode?>?{
        preloadedData.forEach {item ->
            findRecursive(item, id, null)?.let { return it }
        }
        return null
    }


    private fun findRecursive(node: WebTreeNode, nodeId: String, parentNode:WebTreeNode?): Pair<WebTreeNode?, WebTreeNode?>? {
        if (node.id == nodeId) {
            return Pair(node, parentNode)
        }
        node.children.forEach {
            findRecursive(it, nodeId, node)?.let { return it }
        }
        return null
    }

    override fun select(id: String) {
        if (initialized) {
            val node = treeJq.tree("find", id)
            if (node != null) {
                treeJq.tree("select", node.target)
            }
        }
    }


    override fun updateText(id: String, text: String) {
        findNode(id)?.text = text
        if(initialized) {
            val node = treeJq.tree("find", id)
            if (node != null) {
                treeJq.tree("update", object {
                    val target = node.target
                    val text = text
                })
            }
        }
    }

    override fun updateUserData(id: String, data: Any?) {
        findNode(id)?.userData = data
        if(initialized) {
            val node = treeJq.tree("find", id)
            if (node != null) {
                node.userData = data
            }
        }
    }

    override fun getData(): List<WebTreeNode> {
        val roots = treeJq.tree("getRoots")
        val result = arrayListOf<WebTreeNode>()
        roots.forEach { nodeElm ->
            result.add(toWebTreeNode(nodeElm))
        }
        return result
    }

    override fun append(webTreeNode: WebTreeNode, parentId: String) {
        findNode(parentId)?.children?.add(0, webTreeNode)?:return
        if (initialized) {
            treeJq.tree("append", object {
                val parent = treeJq.tree("find", parentId).target
                val data = arrayOf(toTreeItem(webTreeNode))
            })
        }
    }


    override fun remove(id: String) {
        findNodeInternal(id)?.let {pair ->
            val parentNode = pair.second
            if(parentNode != null){
                parentNode.children.removeAll{it.id == id}
            } else {
                preloadedData.removeAll{it.id == id}
            }
            if(initialized){
                val treeItem = treeJq.tree("find", id)
                if(treeItem != null){
                    treeJq.tree("remove", treeItem.target)
                }
            }
        }
    }

    override fun insertAfter(node: WebTreeNode, targetId: String) {
        findNodeInternal(targetId)?.let {pair ->
            val coll = pair.second?.children?:preloadedData
            val idx =coll.indexOfFirst { it.id == targetId }
            coll.add(idx, node)
            if (initialized) {
                treeJq.tree("insert", object {
                    val after = treeJq.tree("find", targetId).target
                    val data = arrayOf(toTreeItem(node))
                })
            }
        }
    }

    override fun insertBefore(node: WebTreeNode, targetId: String) {
        findNodeInternal(targetId)?.let {pair ->
            val coll = pair.second?.children?:preloadedData
            val idx =coll.indexOfFirst { it.id == targetId }
            coll.add(idx, node)
            if (initialized) {
                treeJq.tree("insert", object {
                    val before = treeJq.tree("find", targetId).target
                    val data = arrayOf(toTreeItem(node))
                })
            }
        }
    }

    private fun toTreeItem(it: WebTreeNode): dynamic {
        return object {
            val id = it.id
            val text = it.text
            val userData = it.userData
            val children = it.children.map { toTreeItem(it) }.toTypedArray()
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return HtmlUtilsJS.div(id = "treeDiv${uid}", style = "${config.width?.let { "width:$it" } ?: ""};${config.height?.let { "height:$it" } ?: ""}") {
            ul(id = "tree${uid}") {}
        }.toString()
    }

    override fun decorate() {
        treeJq = jQuery("#tree${uid}")
        treeJq.tree(object {
            val dnd = config.enableDnd
            val fit = config.fit
            val onSelect = onSelect@{ node: dynamic ->
                if (node == null) {
                    return@onSelect
                }
                selectionListener?.invoke(toWebTreeNode(node))
            }
            val onDragEnter = { target: dynamic, source: dynamic ->
                this@EasyUiWebTree.onDragEnterListener?.invoke(toWebTreeNode(toNodeData(target)), toWebTreeNode(source))
            }

            val onBeforeDrop = { target: dynamic, source: dynamic, point: dynamic ->
                this@EasyUiWebTree.onBeforeDropListener?.invoke(toWebTreeNode(toNodeData(target)), toWebTreeNode(source),
                        when(point){
                            "append" -> WebTreeInsertNodePoint.APPEND
                            "top" -> WebTreeInsertNodePoint.TOP
                            else ->  WebTreeInsertNodePoint.BOTTOM
                        }
                )
            }

            val onDrop = { target: dynamic, source: dynamic, point: dynamic ->
                this@EasyUiWebTree.onDropListener?.invoke(toWebTreeNode(toNodeData(target)), toWebTreeNode(source),
                        when(point){
                            "append" -> WebTreeInsertNodePoint.APPEND
                            "top" -> WebTreeInsertNodePoint.TOP
                            else ->  WebTreeInsertNodePoint.BOTTOM
                        }
                )
            }
        })
        initialized = true
        setData(ArrayList(preloadedData))
    }

    private fun toNodeData(node: dynamic): dynamic {
        val result = treeJq.tree("getNode", node)
        return result
    }

    private fun toWebTreeNode(node: dynamic): WebTreeNode {
        val result = WebTreeNode(node.id, node.text, node.userData)
        node.children?.forEach { it ->
            result.children.add(toWebTreeNode(it))
        }
        return result
    }

    override fun destroy() {
        //noops
    }

    override fun getId(): String {
        return "treeDiv${uid}"
    }


}