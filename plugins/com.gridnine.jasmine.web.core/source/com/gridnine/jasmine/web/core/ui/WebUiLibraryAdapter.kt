/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.components.*

interface WebUiLibraryAdapter{

    fun showLoader()

    fun hideLoader()

    fun createBorderContainer(configure:WebBorderContainerConfiguration.()->Unit):WebBorderContainer

    fun createTabsContainer(configure:WebTabsContainerConfiguration.()->Unit):WebTabsContainer

    fun createTree(configure:WebTreeConfiguration.()->Unit):WebTree

    fun createLabel(configure:WebLabelConfiguration.()->Unit) : WebLabel

    fun createSearchBox(configure:WebSearchBoxConfiguration.()->Unit):WebSearchBox

    fun <E : BaseIntrospectableObjectJS> createDataGrid(configure: WebDataGridConfiguration<E>.()->Unit): WebDataGrid<E>

    fun createGridContainer(configure:WebGridContainerConfiguration.()->Unit):WebGridLayoutContainer

    fun createDateBox(configure:WebDateBoxConfiguration.()->Unit):WebDateBox

    fun createDateTimeBox(configure:WebDateTimeBoxConfiguration.()->Unit):WebDateTimeBox

    fun createLinkButton(configure:WebLinkButtonConfiguration.()->Unit):WebLinkButton

    fun createNumberBox(configure:WebNumberBoxConfiguration.()->Unit):WebNumberBox

    fun createSelect(configure:WebSelectConfiguration.()->Unit):WebSelect

    fun createTextBox(configure:WebTextBoxConfiguration.()->Unit):WebTextBox

    fun showWindow(component: WebNode)

    fun showNotification(message:String, timeout:Int)

    fun<W:WebNode> showDialog(dialogContent:W, configure:DialogConfiguration<W>.()->Unit):Dialog<W>

    fun createMenuButton(configure:WebMenuButtonConfiguration.()->Unit):WebMenuButton

    fun createBooleanBox(configure:WebBooleanBoxConfiguration.()->Unit):WebBooleanBox

    fun createTableBox(configure:WebTableBoxConfiguration.()->Unit):WebTableBox

    fun createDivsContainer(configure: WebDivsContainerConfiguration.() -> Unit):WebDivsContainer

    fun createTilesContainer(configure: WebTilesContainerConfiguration.() -> Unit):WebTilesContainer

    fun createPanel(configure: WebPanelConfiguration.() -> Unit):WebPanel

    fun showContextMenu(items:List<WebContextMenuItem>, pageX:Int, pageY:Int)

    fun createAccordionContainer(configure:WebAccordionContainerConfiguration.()->Unit):WebAccordionContainer

    fun createTag(tagName:String, id:String?=null):WebTag

    companion object{
        fun get() = EnvironmentJS.getPublished(WebUiLibraryAdapter::class)
    }
}