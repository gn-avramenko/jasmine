/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*

class EasyUiWebLibraryAdapter :WebUiLibraryAdapter{
    override fun showLoader() {
        jQuery.messager.progress()
    }

    override fun hideLoader() {
        jQuery.messager.progress("close")
    }

    override fun createBorderContainer(configure: WebBorderContainerConfiguration.() -> Unit): WebBorderContainer {
        return EasyUiWebBorderContainer(configure)
    }

    override fun createTabsContainer(configure: WebTabsContainerConfiguration.() -> Unit): WebTabsContainer {
       return EasyUiWebTabsContainer(configure)
    }

    override fun createTree(configure: WebTreeConfiguration.() -> Unit): WebTree {
        return EasyUiWebTree(configure)
    }

    override fun createLabel(configure: WebLabelConfiguration.() -> Unit): WebLabel {
        return EasyUiWebLabel(configure)
    }

    override fun createSearchBox(configure: WebSearchBoxConfiguration.() -> Unit):WebSearchBox {
        return EasyUiWebSearchBox(configure)
    }

    override fun <E : BaseIntrospectableObjectJS> createDataGrid(configure: WebDataGridConfiguration<E>.()->Unit): WebDataGrid<E> {
        return EasyUiWebDataGrid(configure)
    }

    override fun createGridContainer(configure: WebGridContainerConfiguration.() -> Unit): WebGridLayoutContainer {
        return EasyUiWebGridContainer(configure)
    }

    override fun createDateBox(configure: WebDateBoxConfiguration.() -> Unit): WebDateBox {
        return EasyUiWebDateBox(configure)
    }

    override fun createDateTimeBox(configure: WebDateTimeBoxConfiguration.() -> Unit): WebDateTimeBox {
        return EasyUiWebDateTimeBox(configure)
    }

    override fun createLinkButton(configure: WebLinkButtonConfiguration.() -> Unit): WebLinkButton {
        return EasyUiWebLinkButton(configure)
    }

    override fun createNumberBox(configure: WebNumberBoxConfiguration.() -> Unit): WebNumberBox {
        return EasyUiWebNumberBox(configure)
    }

    override fun createSelect(configure: WebSelectConfiguration.() -> Unit): WebSelect {
        return EasyUiWebSelect(configure)
    }

    override fun createTextBox(configure: WebTextBoxConfiguration.() -> Unit): WebTextBox {
        return EasyUiWebTextBox(configure)
    }

    override fun showWindow(component: WebNode) {
        if(EnvironmentJS.test){
            return
        }
        findEasyUiComponent(component).let {
            jQuery("body").html(it.getHtml())
            it.decorate()
        }
    }

}



