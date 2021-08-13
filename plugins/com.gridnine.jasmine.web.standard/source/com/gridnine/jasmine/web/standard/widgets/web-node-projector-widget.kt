/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window

class WebNodeProjectorWidget(configure:WebNodeProjectorWidgetConfiguration.()->Unit) : BaseWebNodeWrapper<WebTag>() {
    private val uuid = MiscUtilsJS.createUUID()
    private var activeComponentId:String? = null
    private var initialized = false
    private val nodes = arrayListOf<WebProjectorNodeData>()

    init {
        _node = WebUiLibraryAdapter.get().createTag("div", "divsContainer${uuid}")
        val config = WebNodeProjectorWidgetConfiguration()
        config.configure()
        config.width?.let { _node.getStyle().setParameters("width" to it) }
        config.height?.let { _node.getStyle().setParameters("height" to it) }
        _node.setPostRenderAction {
            initialized = true
            activeComponentId?.let {
                window.setTimeout({showInternal(it)}, 10)
            }
        }
    }

    private fun showInternal(id: String) {
        val tags = _node.getChildren() as List<WebTag>
        if(activeComponentId != null){
            val actClDiv = getDivId(activeComponentId!!)
            tags.find { it.getId() == actClDiv }?.setVisible(false)
        }
        val clDiv = getDivId(id)
        val existingNode = tags.find { it.getId() == clDiv }
        if(existingNode != null){
            existingNode.setVisible(true)
            return
        }
        val data = nodes.find { it.id == id }?:throw XeptionJS.forDeveloper("unable to find node with id $id")
        val wrapperTab = WebUiLibraryAdapter.get().createTag("div", clDiv).also {
            it.getStyle().setParameters("width" to "100%", "height" to "100%")
        }
        _node.getChildren().addChild(wrapperTab)
        wrapperTab.getChildren().addChild(data.content)
    }

    fun addNode(id: String, content: WebNode) {
        nodes.add(WebProjectorNodeData(id, content))
    }

    fun showNode(id: String) {
        if(activeComponentId != id){
            if(initialized){
                showInternal(id)
            }
            activeComponentId = id
        }
    }

    fun removeNode(id: String) {
        val clDiv = getDivId(id)
        nodes.find { it.id == id }?.let { nodes.remove(it) }
        val tags = _node.getChildren() as List<WebTag>
        tags.find { it.getId() ==clDiv }?.let {  _node.getChildren().removeChild(it) }
        if(id == activeComponentId){
            activeComponentId = null
        }
    }

    fun getNode(id: String): WebNode? {
        return nodes.find { it.id == id }?.content
    }

    fun clear() {
        _node.getChildren().clear()
        nodes.clear()
    }

    fun getActiveNodeId(): String? {
        return activeComponentId
    }

    private fun getDivId(id: String): String {
        return "div${id.replace(".","_")}${uuid}"
    }
}

class WebNodeProjectorWidgetConfiguration: BaseWidgetConfiguration()

internal class WebProjectorNodeData(val id:String, val content:WebNode)