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

    companion object{
        fun get() = EnvironmentJS.getPublished(WebUiLibraryAdapter::class)
    }
}