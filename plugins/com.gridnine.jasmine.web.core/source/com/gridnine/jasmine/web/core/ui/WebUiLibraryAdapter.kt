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

    fun showWindow(component: WebNode)

    companion object{
        fun get() = EnvironmentJS.getPublished(WebUiLibraryAdapter::class)
    }
}