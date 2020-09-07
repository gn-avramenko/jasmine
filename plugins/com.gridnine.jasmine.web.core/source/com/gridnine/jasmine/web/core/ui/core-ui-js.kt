/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.components.*

interface UiLibraryAdapter{
    fun showWindow(component: WebComponent)
    fun createBorderLayout(parent: WebComponent?, configure:WebBorderLayoutConfiguration.()->Unit):WebBorderContainer
    fun createLabel(parent: WebComponent?):WebLabel
    fun createAccordionContainer(parent: WebComponent?, configure:WebAccordionPanelConfiguration.()->Unit):WebAccordionContainer
    fun createTabsContainer(parent: WebComponent?, configure:WebTabsContainerConfiguration.()->Unit):WebTabsContainer
    fun<E:Any> createDataList(parent: WebComponent?, configure:WebDataListConfiguration.()->Unit):WebDataList<E>
    companion object{
        fun get() = EnvironmentJS.getPublished(UiLibraryAdapter::class)
    }

}

interface WebComponent{
    fun getParent():WebComponent?
    fun getChildren():MutableList<WebComponent>
    fun getHtml():String
    fun decorate()
}

external var debugger: dynamic = definedExternally