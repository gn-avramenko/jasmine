/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.TreeConfiguration
import com.gridnine.jasmine.server.core.ui.components.TreeContextMenuEvent
import com.gridnine.jasmine.server.core.ui.components.TreeItem
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.DropEvent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zul.Div
import org.zkoss.zul.Tree
import org.zkoss.zul.Treechildren
import org.zkoss.zul.Treeitem
import java.util.*

open class ZkTree(configure: TreeConfiguration.() -> Unit) : com.gridnine.jasmine.server.core.ui.components.Tree, ZkUiComponent {

    private val data = arrayListOf<TreeItem>()

    private var treeComponent: Tree? = null

    private var component: Div? = null

    private var selectListener: ((TreeItem) -> Unit)? = null

    private var selectListenerSet = false

    private var contextMenuListener: ((node: TreeItem, uiComp: TreeContextMenuEvent) -> Unit)? = null

    private var dropListener: ((target: TreeItem, source: TreeItem) -> Unit)? = null

    private val treeUid = UUID.randomUUID().toString()

    private val config = TreeConfiguration()
    init {
        config.configure()
    }
    override fun setData(data: List<TreeItem>) {
        this.data.clear()
        this.data.addAll(data)
        if (treeComponent != null) {
            setDataInternal()
        }
    }

    override fun setSelectListener(listener: ((item: TreeItem) -> Unit)?) {
        this.selectListener = listener
        if (listener != this.selectListener) {
            this.selectListener = listener
            if (!selectListenerSet && treeComponent != null) {
                setSelectListenerInternal()
            }
        }
    }

    override fun setOnContextMenuListener(listener: ((node: TreeItem, uiComp: TreeContextMenuEvent) -> Unit)?) {
        this.contextMenuListener = listener
        if (listener != this.contextMenuListener) {
            this.contextMenuListener = listener
        }
    }

    override fun setOnDropListener(listener: ((target: TreeItem, source: TreeItem) -> Unit)?) {
        this.dropListener = listener
    }

    override fun insertAfter(data: TreeItem, targetId: String) {
        val node = findNodeInternal(targetId)!!
            val item = createItem(data)
            val parent = node.parent
            parent.insertBefore(item, node)
            parent.removeChild(node)
            parent.insertBefore(node, item)
    }

    override fun insertBefore(data: TreeItem, targetId: String) {
        val node = findNodeInternal(targetId)!!
            val item = createItem(data)
            node.parent.insertBefore(item, node)
    }

    override fun append(data: TreeItem, parentId: String) {
        val node = findNodeInternal(parentId)!!
        if(node.treechildren == null){
            node.appendChild(Treechildren())
        }
        node.treechildren.appendChild(createItem(data))
    }

    override fun findNode(id: String): TreeItem? {
        return findNodeInternal(id)?.getValue()
    }

    override fun select(id: String) {
        val node = findNodeInternal(id)
        if(node != null){
            treeComponent!!.selectItem(node)
            selectListener?.invoke(node.getValue())
        }
    }

    override fun updateText(id: String, text: String) {
        findNodeInternal(id)?.let { it.label = text }
    }

    override fun updateUserData(id: String, data: Any?) {
        findNodeInternal(id)?.getValue<TreeItem>()!!.userData = (data)
    }

    override fun getData(): List<TreeItem> {
        return getData(treeComponent!!.treechildren)
    }

    private fun getData(treechildren: Treechildren): List<TreeItem> {
        val result = arrayListOf<TreeItem>()
        treechildren.getChildren<Treeitem>().forEach {
            val itemValue = it.getValue() as TreeItem
            val item = TreeItem(itemValue.id, it.label, itemValue.userData)
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
            val value = child.getValue() as TreeItem
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
        val children = comp.getChildren<Treechildren>()
        if(children.isNotEmpty()){
            children.clear()
        }
        val rootChildren = Treechildren()
        rootChildren.parent = comp
        data.forEach {
            val treeItem = createItem(it)
            treeItem.parent = rootChildren
        }
    }

    private fun createItem(data: TreeItem): Treeitem {
        val result = Treeitem()
        result.isOpen = data.children.isNotEmpty()
        result.label = data.text
        result.id = "${treeUid}||${data.id}"
        result.setValue(data)
        if (config.enableDnd) {
            result.draggable = "true"
            result.droppable = "true"
        }
        if (contextMenuListener != null) {
            result.addEventListener(Events.ON_RIGHT_CLICK) {
                it as MouseEvent
                it.stopPropagation()
                contextMenuListener?.invoke(data, TreeContextMenuEvent(it.pageX, it.pageY))
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

    override fun getZkComponent(): HtmlBasedComponent {
        if(this.component != null){
            return this.component!!
        }
        val div = Div()
        configureDimensions(div, config)
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
            treeComponent!!.addEventListener(Events.ON_CLICK) {
                it as MouseEvent
                val target = it.target as Tree
                val items = target.selectedItems
                if (items.isNotEmpty()) {
                    val obj = items.iterator().next() as Treeitem
                    val data = obj.getValue<TreeItem>()
                    selectListener!!.invoke(data)
                }
            }
            selectListenerSet = true
        }
    }


}