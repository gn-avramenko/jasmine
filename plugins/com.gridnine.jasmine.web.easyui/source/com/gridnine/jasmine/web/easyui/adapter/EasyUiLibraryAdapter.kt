/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter

import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.easyui.adapter.elements.*

class EasyUiLibraryAdapter:UiLibraryAdapter {
    override fun showWindow(component: WebComponent) {
        jQuery("body").html(component.getHtml())
        component.decorate()
    }

    override fun createBorderLayout(parent: WebComponent?, configure: WebBorderLayoutConfiguration.() -> Unit): WebBorderContainer {
        return EasyUiWebBorderContainer(parent, configure)
    }

    override fun createLabel(parent: WebComponent?): WebLabel {
        return EasyUiWebLabel(parent)
    }

    override fun createAccordionContainer(parent: WebComponent?, configure: WebAccordionPanelConfiguration.() -> Unit): WebAccordionContainer {
        return EasyUiWebAccordionContainer(parent, configure)
    }

    override fun createTabsContainer(parent: WebComponent?, configure: WebTabsContainerConfiguration.() -> Unit): WebTabsContainer {
        return EasyUiWebTabsContainer(parent, configure)
    }

    override fun <E : Any> createDataList(parent: WebComponent?, configure: WebDataListConfiguration.() -> Unit): WebDataList<E> {
        return EasyUiWebDataList(parent, configure)
    }


}

external var jQuery: dynamic = definedExternally