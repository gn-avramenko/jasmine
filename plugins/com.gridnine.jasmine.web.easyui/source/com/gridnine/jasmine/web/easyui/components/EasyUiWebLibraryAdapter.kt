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



