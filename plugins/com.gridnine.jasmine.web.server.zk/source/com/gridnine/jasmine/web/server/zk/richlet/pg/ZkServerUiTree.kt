/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.DropEvent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zk.ui.event.SelectEvent
import org.zkoss.zul.Div
import org.zkoss.zul.Tree
import org.zkoss.zul.Treechildren
import org.zkoss.zul.Treeitem

open class ZkServerUiTree(private val config: ServerUiTreeConfiguration) : ServerUiTree, ZkServerUiComponent() {

    private val data = arrayListOf<ServerUiTreeItem>()

    private var treeComponent: Tree? = null

    private var component: Div? = null

    private var selectListener: ((ServerUiTreeItem) -> Unit)? = null

    private var selectListenerSet = false

    private var contextMenuListener: ((node: ServerUiTreeItem, uiComp: ServerUiTreeContextMenuEvent) -> Unit)? = null


    private var dropListener: ((target: ServerUiTreeItem, source: ServerUiTreeItem) -> Unit)? = null

    override fun setData(data: List<ServerUiTreeItem>) {
        this.data.clear()
        this.data.addAll(data)
        if (treeComponent != null) {
            setDataInternal()
        }
    }

    override fun setSelectListener(listener: ((item: ServerUiTreeItem) -> Unit)?) {
        this.selectListener = listener
        if (listener != this.selectListener) {
            this.selectListener = listener
            if (!selectListenerSet && treeComponent != null) {
                setSelectListenerInternal()
            }
        }
    }

    override fun setOnContextMenuListener(listener: ((node: ServerUiTreeItem, uiComp: ServerUiTreeContextMenuEvent) -> Unit)?) {
        this.contextMenuListener = listener
        if (listener != this.contextMenuListener) {
            this.contextMenuListener = listener
        }
    }

    override fun setOnDropListener(listener: ((target: ServerUiTreeItem, source: ServerUiTreeItem) -> Unit)?) {
        this.dropListener = listener
    }

    override fun insertAfter(data: ServerUiTreeItem, targetId: String) {
        val node = findNodeInternal(targetId)!!
            val item = createItem(data)
            val parent = node.parent
            parent.insertBefore(item, node)
            parent.removeChild(node)
            parent.insertBefore(node, item)
    }

    override fun insertBefore(data: ServerUiTreeItem, targetId: String) {
        val node = findNodeInternal(targetId)!!
            val item = createItem(data)
            node.parent.insertBefore(item, node)
    }

    override fun append(data: ServerUiTreeItem, parentId: String) {
        val node = findNodeInternal(parentId)!!
        if(node.treechildren == null){
            node.appendChild(Treechildren())
        }
        node.treechildren.appendChild(createItem(data))
    }

    override fun findNode(id: String): ServerUiTreeItem? {
        return findNodeInternal(id)?.getValue()
    }

    override fun select(id: String) {
        val node = findNodeInternal(id)
        if(node != null){
            treeComponent!!.selectItem(node)
        }
    }

    override fun updateText(id: String, text: String) {
        findNodeInternal(id)?.let { it.label = text }
    }

    override fun updateUserData(id: String, data: Any?) {
        findNodeInternal(id)?.setValue(data)
    }

    override fun getData(): List<ServerUiTreeItem> {
        return getData(treeComponent!!.treechildren)
    }

    private fun getData(treechildren: Treechildren): List<ServerUiTreeItem> {
        val result = arrayListOf<ServerUiTreeItem>()
        treechildren.items.forEach {
            val item = ServerUiTreeItem(it.id, it.label, (it.getValue() as ServerUiTreeItem).userData)
            result.add(item)
            if(!it.isEmpty){
                item.children.addAll(getData(it.treechildren))
            }
        }
        return result
    }

    override fun remove(id: String) {
        val node = findNodeInternal(id)
        node?.parent?.removeChild(node)
    }

    private fun findNodeInternal(id: String): Treeitem? {
        val comp = treeComponent!!
        return findNodeInternal(comp.treechildren, id)
    }

    private fun findNodeInternal(children: Treechildren, id: String): Treeitem? {
        for(child in children.items){
            val value = child.getValue() as ServerUiTreeItem
            if(value.id == id){
                return child
            }
            if(!child.isEmpty){
                val node = findNodeInternal(child.treechildren, id)
                if(node != null){
                    return node
                }
            }
        }
        return null
    }


    private fun setDataInternal() {
        val comp = treeComponent!!
        val rootChildren = Treechildren()
        rootChildren.parent = comp
        data.forEach {
            val treeItem = createItem(it)
            treeItem.parent = rootChildren
        }
    }

    private fun createItem(data: ServerUiTreeItem): Treeitem {
        val result = Treeitem()
        result.isOpen = data.children.isNotEmpty()
        result.label = data.text
        result.setValue(data)
        if (config.enableDnd) {
            result.draggable = "true"
            result.droppable = "true"
        }
        if (contextMenuListener != null) {
            result.addEventListener(Events.ON_RIGHT_CLICK) {
                it as MouseEvent
                it.stopPropagation()
                contextMenuListener?.invoke(data, ServerUiTreeContextMenuEvent(it.pageX, it.pageY))
            }
        }
        if(dropListener != null){
            result.addEventListener(Events.ON_DROP) {
                it as DropEvent
                it.stopPropagation()
                val source = it.dragged as Treeitem
                dropListener?.invoke(data, source.getValue())
            }
        }
        if(data.children.isNotEmpty()){
            val children = Treechildren()
            children.parent = result
            data.children.forEach {
                val treeItem = createItem(it)
                treeItem.parent = children
            }
        }

        return result
    }

    override fun getComponent(): HtmlBasedComponent {
        if(this.component != null){
            return this.component!!
        }
        val div = Div()
        if (config.width == "100%") {
            div.hflex = "1"
        } else if (config.width != null) {
            div.width = "width"
        }
        if (config.height == "100%") {
            div.vflex = "1"
        } else if (config.height != null) {
            div.height = config.height
        }
        val comp = Tree()
        comp.vflex = "1"
        comp.hflex = "1"
        comp.parent = div
        treeComponent = comp
        component = div
        setDataInternal()
        if (selectListener != null) {
            setSelectListenerInternal()
        }
        return div
    }

    private fun setSelectListenerInternal() {
        if (!selectListenerSet) {
            treeComponent!!.addEventListener(Events.ON_SELECT) {
                it as SelectEvent<*, *>
                val obj = it.selectedItems.iterator().next() as Treeitem
                val data = obj.getValue<ServerUiTreeItem>()
                selectListener!!.invoke(data)
            }
            selectListenerSet = true
        }
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}