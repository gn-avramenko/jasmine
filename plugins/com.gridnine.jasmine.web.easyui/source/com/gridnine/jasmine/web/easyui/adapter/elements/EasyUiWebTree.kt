/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebLabel
import com.gridnine.jasmine.web.core.ui.components.WebTree
import com.gridnine.jasmine.web.core.ui.components.WebTreeConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebTreeNode
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTree(private val parent:WebComponent?, configure: WebTreeConfiguration.() -> Unit) :WebTree{

    private val config = WebTreeConfiguration()
    private val uid = MiscUtilsJS.createUUID()
    private var initialized = false
    private var treeJq : dynamic = null
    private val preloadedData = arrayListOf<WebTreeNode>()
    private var selectionListener: ((item: WebTreeNode) -> Unit)? = null
    init {
        config.configure()
    }
    override fun setData(data: List<WebTreeNode>) {
        if(!initialized) {
            this.preloadedData.clear()
            this.preloadedData.addAll(data)
        }
        if(initialized){
            treeJq.tree("loadData",data.map {
                toTreeItem(it)
            }.toTypedArray())
        }
    }

    override fun setSelectListener(listener: ((item: WebTreeNode) -> Unit)?) {
        selectionListener = listener
    }

    override fun findNode(id: String): WebTreeNode? {
        val node = treeJq.tree("find", id)
        return if(node == null) null else toWebTreeNode(node)
    }

    override fun select(id: String) {
        val node = treeJq.tree("find", id)
        if(node != null){
            treeJq.tree("select", node.target)
        }
    }

    override fun updateText(id: String, text: String) {
        val node = treeJq.tree("find", id)
        if(node != null) {
            treeJq.tree("update", object {
                val target = node.target
                val text = text
            })
        }
    }

    override fun updateUserData(id: String, data: Any?) {
        val node = treeJq.tree("find", id)
        if(node != null){
            node.attributes.userData = data
        }
    }

    override fun getData(): List<WebTreeNode> {
        val roots = treeJq.tree("getRoots")
        val result = arrayListOf<WebTreeNode>()
        roots.forEach{ nodeElm ->
            result.add(toWebTreeNode(nodeElm))
        }
        return result
    }

    private fun toTreeItem(it: WebTreeNode): dynamic {
        return object{
            val id = it.id
            val text = it.text
            val attributes = object{
                val userData = it.userData
            }
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
        return HtmlUtilsJS.div (id = "treeDiv${uid}", style = "${config.width?.let { "width:$it" }?:""};${config.height?.let { "height:$it" }?:""}") {
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
        })
        initialized = true
        setData(ArrayList(preloadedData))
    }

    private fun toWebTreeNode(node: dynamic): WebTreeNode {
        val result = WebTreeNode(node.id, node.text, node.attributes.userData)
        node.children?.forEach{ it ->
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