/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element

class AntdWebUiLibraryAdapter:WebUiLibraryAdapter {

    override fun showLoader() {
        val bodyElm = document.getElementsByTagName("body").item(0)!!
        document.getElementById("loader")?: kotlin.run {
            val divElement = document.createElement("div")
            divElement.setAttribute("id", "loader")
            divElement.asDynamic().className = "loader"
            bodyElm.appendChild(divElement)
            val facade = window.asDynamic().ReactFacade
            facade.render(ReactFacade.createElement(ReactFacade.Spin, object{val size="large"}), divElement)
        }
        bodyElm.asDynamic().classList.add("loading")
    }

    override fun hideLoader() {
        val bodyElm = document.getElementsByTagName("body").item(0)!!
        bodyElm.asDynamic().classList.remove("loading")
    }

    override fun createBorderContainer(configure: WebBorderContainerConfiguration.() -> Unit): WebBorderContainer {
        return AntdWebBorderContainer(configure)
    }

    override fun createTabsContainer(configure: WebTabsContainerConfiguration.() -> Unit): WebTabsContainer {
        return AntdWebTabsContainer(configure)
    }

    override fun createTree(configure: WebTreeConfiguration.() -> Unit): WebTree {
        val config = WebTreeConfiguration()
        config.configure()
        if(config.mold == WebTreeMold.NAVIGATION){
            return AntdWebNavigationTree(config)
        }
        TODO("Not yet implemented")
    }

    override fun createSearchBox(configure: WebSearchBoxConfiguration.() -> Unit): WebSearchBox {
        TODO("Not yet implemented")
    }

    override fun <E : BaseIntrospectableObjectJS> createDataGrid(configure: WebDataGridConfiguration<E>.() -> Unit): WebDataGrid<E> {
        TODO("Not yet implemented")
    }

    override fun createDateBox(configure: WebDateBoxConfiguration.() -> Unit): WebDateBox {
        TODO("Not yet implemented")
    }

    override fun createDateTimeBox(configure: WebDateTimeBoxConfiguration.() -> Unit): WebDateTimeBox {
        TODO("Not yet implemented")
    }

    override fun createLinkButton(configure: WebLinkButtonConfiguration.() -> Unit): WebLinkButton {
        TODO("Not yet implemented")
    }

    override fun createNumberBox(configure: WebNumberBoxConfiguration.() -> Unit): WebNumberBox {
        TODO("Not yet implemented")
    }

    override fun createSelect(configure: WebSelectConfiguration.() -> Unit): WebSelect {
        TODO("Not yet implemented")
    }

    override fun createTextBox(configure: WebTextBoxConfiguration.() -> Unit): WebTextBox {
        TODO("Not yet implemented")
    }

    override fun createPasswordBox(configure: WebPasswordBoxConfiguration.() -> Unit): WebPasswordBox {
        TODO("Not yet implemented")
    }

    override fun showWindow(component: WebNode) {
        ReactFacade.render(findAntdComponent(component).getReactElement(), document.getElementsByTagName("body").item(0) as Element)
    }

    override fun showNotification(message: String, timeout: Int) {
        TODO("Not yet implemented")
    }

    override fun <W : WebNode> showDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> {
        TODO("Not yet implemented")
    }

    override fun createMenuButton(configure: WebMenuButtonConfiguration.() -> Unit): WebMenuButton {
        TODO("Not yet implemented")
    }

    override fun createBooleanBox(configure: WebBooleanBoxConfiguration.() -> Unit): WebBooleanBox {
        TODO("Not yet implemented")
    }

    override fun createPanel(configure: WebPanelConfiguration.() -> Unit): WebPanel {
        TODO("Not yet implemented")
    }

    override fun showContextMenu(items: List<WebContextMenuItem>, pageX: Int, pageY: Int) {
        TODO("Not yet implemented")
    }

    override fun createAccordionContainer(configure: WebAccordionContainerConfiguration.() -> Unit): WebAccordionContainer {
        TODO("Not yet implemented")
    }

    override fun createTag(tagName: String, id: String?): WebTag {
        return AntdWebTag(tagName, id)
    }

    override fun createRichTextEditor(configure: WebRichTextEditorConfiguration.() -> Unit): WebRichTextEditor {
        TODO("Not yet implemented")
    }
}