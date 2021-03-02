/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.web.server.components.*
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.HtmlBasedComponent

abstract class ZkServerUiComponent: ServerUiNode {
    var parent: ZkServerUiComponent? = null
    abstract fun getZkComponent():HtmlBasedComponent
}

fun findZkComponent(node: ServerUiNode):ZkServerUiComponent{
    if(node is ServerUiNodeWrapper<*>){
        return findZkComponent(node.getNode())
    }
    return node as ZkServerUiComponent
}
class ZkServerUiLibraryAdapter:ServerUiLibraryAdapter{
    override fun redirect(relativeUrl: String) {
        Executions.getCurrent().sendRedirect(relativeUrl)
    }

    override fun createBorderLayout(config: ServerUiBorderContainerConfiguration): ServerUiBorderContainer {
        return ZkServerUiBorderContainer(config)
    }

    override fun createLabel(config: ServerUiLabelConfiguration): ServerUiLabel {
        return ZkServerUiLabel(config)
    }

    override fun createAccordionContainer(config: ServerUiAccordionContainerConfiguration): ServerUiAccordionContainer {
        return ZkServerUiAccordionContainer(config)
    }

    override fun createTabboxContainer(config: ServerUiTabboxConfiguration): ServerUiTabbox {
        return ZkServerUiTabbox(config)
    }

    override fun createGridLayoutContainer(config: ServerUiGridLayoutContainerConfiguration): ServerUiGridLayoutContainer {
        return ZkServerUiGridLayoutContainer(config)
    }

    override fun <E : BaseIntrospectableObject> createDataGrid(config: ServerUiDataGridComponentConfiguration): ServerUiDataGridComponent<E> {
        return ZkDataGridComponent(config)
    }

    override fun createTextBox(config: ServerUiTextBoxConfiguration): ServerUiTextBox {
        return ZkServerUiTextBox(config)
    }

    override fun createPasswordBox(config: ServerUiPasswordBoxConfiguration): ServerUiPasswordBox {
        return ZkServerUiPasswordBox(config)
    }

    override fun createLinkButton(config: ServerUiLinkButtonConfiguration): ServerUiLinkButton {
        return ZkServerUiLinkButton(config)
    }

    override fun createDateBox(config: ServerUiDateBoxConfiguration): ServerUiDateBox {
        return ZkServerUiDateBox(config)
    }

    override fun createDateTimeBox(config: ServerUiDateTimeBoxConfiguration): ServerUiDateTimeBox {
        return ZkServerUiDateTimeBox(config)
    }

    override fun createNumberBox(config: ServerUiNumberBoxConfiguration): ServerUiNumberBox {
        return ZkServerUiNumberBox(config)
    }

    override fun createSelect(config: ServerUiSelectConfiguration): ServerUiSelect {
        return ZkServerUiSelect(config)
    }

    override fun <W : ServerUiNode> showDialog(config: ServerUiDialogConfiguration<W>): ServerUiDialog<W> {
        return com.gridnine.jasmine.web.server.zk.components.showDialog(config)
    }

    override fun createMenuButton(config: ServerUiMenuButtonConfiguration): ServerUiMenuButton {
        return ZkServerUiMenuButton(config)
    }

    override fun createPanel(config: ServerUiPanelConfiguration): ServerUiPanel {
        return ZkServerUiPanel(config)
    }

    override fun createTilesContainer(config: ServerUiTilesContainerConfiguration): ServerUiTilesContainer {
        return ZkServerUiTilesContainer(config)
    }

    override fun createDivsContainer(config: ServerUiDivsContainerConfiguration): ServerUiDivsContainer {
        return ZkServerUiDivsContainer(config)
    }

    override fun createBooleanBox(config: ServerUiBooleanBoxConfiguration): ServerUiBooleanBox {
        return ZkServerUiBooleanBox(config)
    }

    override fun createTableBox(config: ServerUiTableConfiguration): ServerUiTable {
        return ZkServerUiTable(config)
    }

    override fun createTree(config: ServerUiTreeConfiguration): ServerUiTree {
        return ZkServerUiTree(config)
    }

    override fun showContextMenu(items: List<ServerUiContextMenuItem>, pageX: Int, pageY: Int) {
        zkShowContextMenu(items, pageX, pageY)
    }

    override fun showNotification(message: String, type:ServerUiNotificationType, timeout: Int) {
        zkShowNotification(message, type, timeout)
    }

    override fun findRootComponent(): ServerUiNode {
        return Executions.getCurrent().desktop.firstPage.firstRoot.attributes["rootComponent"] as ServerUiNode
    }

}