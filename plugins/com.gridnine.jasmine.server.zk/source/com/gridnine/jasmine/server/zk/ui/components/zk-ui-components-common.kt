/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.NotificationType
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.web.server.zk.components.ZkBorderContainer
import com.gridnine.jasmine.web.server.zk.components.ZkDataGrid
import com.gridnine.jasmine.web.server.zk.components.ZkLabel
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.HtmlBasedComponent

interface ZkUiComponent: UiNode {
    fun getZkComponent():HtmlBasedComponent
}

fun findZkComponent(node: UiNode): ZkUiComponent {
    if(node is BaseNodeWrapper<*>){
        return findZkComponent(node.getNode())
    }
    return node as ZkUiComponent
}

fun configureBasicParameters(component:HtmlBasedComponent, configuration:BaseComponentConfiguration){
    val width = configuration.width
    component.width = width
    val height = configuration.height
    if (height == "100%") {
        component.vflex = "1"
    } else {
        component.height = height
    }
    component.setClass(configuration.sClass)
}
class ZkUiLibraryAdapter:UiLibraryAdapter{
    override fun redirect(relativeUrl: String) {
        Executions.getCurrent().sendRedirect(relativeUrl)
    }

    override fun createBorderLayout(configure: BorderContainerConfiguration.() -> Unit): BorderContainer {
        return ZkBorderContainer(configure)
    }

    override fun createLabel(configure: LabelConfiguration.() -> Unit): Label {
        return ZkLabel(configure)
    }

    override fun createAccordionContainer(configure: AccordionContainerConfiguration.() -> Unit): AccordionContainer {
        return ZkAccordionContainer(configure)
    }

    override fun createTabboxContainer(configure: TabboxConfiguration.() -> Unit): Tabbox {
        return ZkTabbox(configure)
    }

    override fun createGridLayoutContainer(configure: GridLayoutContainerConfiguration.() -> Unit): GridLayoutContainer {
        return ZkGridLayoutContainer(configure)
    }

    override fun <E : BaseIntrospectableObject> createDataGrid(configure: DataGridConfiguration.() -> Unit): DataGrid<E> {
        return ZkDataGrid(configure)
    }

    override fun createTextBox(configure: TextBoxComponentConfiguration.() -> Unit): TextBox {
        return ZkTextBox(configure)
    }

    override fun createPasswordBox(configure: PasswordBoxComponentConfiguration.() -> Unit): PasswordBox {
        return ZkPasswordBox(configure)
    }

    override fun createLinkButton(configure: LinkButtonConfiguration.() -> Unit): LinkButton {
        return ZkLinkButton(configure)
    }

    override fun createDateBox(configure: DateBoxComponentConfiguration.() -> Unit): DateBox {
        return ZkDateBox(configure)
    }

    override fun createDateTimeBox(configure: DateTimeBoxComponentConfiguration.() -> Unit): DateTimeBox {
        return ZkDateTimeBox(configure)
    }

    override fun createNumberBox(configure: NumberBoxComponentConfiguration.() -> Unit): NumberBox {
        return ZkNumberBox(configure)
    }

    override fun createSelect(configure: SelectConfiguration.() -> Unit): Select {
        return ZkSelect(configure)
    }

    override fun <W : UiNode> showDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> {
        return zkShowDialog(dialogContent, configure)
    }

    override fun createMenuButton(configure: MenuButtonConfiguration.() -> Unit): MenuButton {
        return ZkMenuButton(configure)
    }

    override fun createPanel(configure: PanelConfiguration.() -> Unit): Panel {
        return  ZkPanel(configure)
    }

    override fun createTilesContainer(configure: TilesContainerConfiguration.() -> Unit): TilesContainer {
        return ZkTilesContainer(configure)
    }

    override fun createDivsContainer(configure: DivsContainerConfiguration.() -> Unit): DivsContainer {
        return ZkDivsContainer(configure)
    }

    override fun createBooleanBox(configure: BooleanBoxComponentConfiguration.() -> Unit): BooleanBox {
        return ZkBooleanBox(configure)
    }

    override fun createTable(configure: TableConfiguration.() -> Unit): Table {
        return ZkTable(configure)
    }

    override fun createTree(configure: TreeConfiguration.() -> Unit): Tree {
        return ZkTree(configure)
    }

    override fun showContextMenu(items: List<ContextMenuItem>, pageX: Int, pageY: Int) {
        zkShowContextMenu(items, pageX, pageY)
    }

    override fun showNotification(message: String, type: NotificationType, timeout: Int) {
        zkShowNotification(message, type, timeout)
    }


    override fun findRootComponent(): UiNode {
        return Executions.getCurrent().desktop.firstPage.firstRoot.attributes["rootComponent"] as UiNode
    }

}