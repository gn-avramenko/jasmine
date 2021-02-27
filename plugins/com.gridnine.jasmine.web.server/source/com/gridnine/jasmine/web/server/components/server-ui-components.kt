/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.web.WebServer



interface ServerUiNode{
    fun getParent(): ServerUiNode?
}

interface ServerUiNodeWrapper<T:ServerUiNode>: ServerUiNode{
    fun getNode():T
}

abstract class BaseServerUiNodeWrapper<T:ServerUiNode>:ServerUiNodeWrapper<T>{
    private var _parent:ServerUiNode? =  null
    protected lateinit var _node:T

    fun setParent(parent:ServerUiNode){
        this._parent = parent
    }

    override fun getParent(): ServerUiNode? {
        return _parent
    }

    override fun getNode(): T {
        return _node
    }
}


enum class ServerUiComponentHorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}

interface ServerUiLibraryAdapter{
    fun redirect(relativeUrl:String)
    fun createBorderLayout(config:ServerUiBorderContainerConfiguration):ServerUiBorderContainer
    fun createLabel(config:ServerUiLabelConfiguration):ServerUiLabel
    fun createAccordionContainer(config:ServerUiAccordionContainerConfiguration):ServerUiAccordionContainer
    fun createTabboxContainer(config:ServerUiTabboxConfiguration):ServerUiTabbox
    fun createGridLayoutContainer(config:ServerUiGridLayoutContainerConfiguration):ServerUiGridLayoutContainer
    fun<E:BaseIntrospectableObject> createDataGrid(config:ServerUiDataGridComponentConfiguration):ServerUiDataGridComponent<E>
    fun createTextBox(config:ServerUiTextBoxConfiguration):ServerUiTextBox
    fun createPasswordBox(config:ServerUiPasswordBoxConfiguration):ServerUiPasswordBox
    fun createLinkButton(config:ServerUiLinkButtonConfiguration):ServerUiLinkButton
    fun createDateBox(config:ServerUiDateBoxConfiguration):ServerUiDateBox
    fun createDateTimeBox(config:ServerUiDateTimeBoxConfiguration):ServerUiDateTimeBox
    fun createNumberBox(config:ServerUiNumberBoxConfiguration):ServerUiNumberBox
    fun createSelect(config:ServerUiSelectConfiguration): ServerUiSelect
    fun<W> showDialog(config:ServerUiDialogConfiguration<W>):ServerUiDialog<W>  where W:ServerUiNode
    fun createMenuButton(config:ServerUiMenuButtonConfiguration):ServerUiMenuButton
    fun createPanel(config:ServerUiPanelConfiguration):ServerUiPanel
    fun createTilesContainer(config:ServerUiTilesContainerConfiguration):ServerUiTilesContainer
    fun createDivsContainer(config:ServerUiDivsContainerConfiguration):ServerUiDivsContainer
    fun createBooleanBox(config:ServerUiBooleanBoxConfiguration):ServerUiBooleanBox
    fun createTableBox(config:ServerUiTableConfiguration):ServerUiTable
    fun createTree(config:ServerUiTreeConfiguration):ServerUiTree
    fun showContextMenu(items:List<ServerUiContextMenuItem>, pageX:Int, pageY:Int)
    fun showNotification(message:String, timeout:Int)


    companion object{
        private val wrapper = PublishableWrapper(ServerUiLibraryAdapter::class)
        fun get() = wrapper.get()
    }
}