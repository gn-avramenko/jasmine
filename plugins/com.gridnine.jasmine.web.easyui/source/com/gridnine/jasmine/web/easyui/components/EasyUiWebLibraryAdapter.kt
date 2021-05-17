/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

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

    override fun showNotification(message: String, timeout: Int) {
            jQuery.messager.show(object{
                val msg = message
                val timeout = timeout
                val showType = "show"
            })
    }


    override fun <W : WebNode> showDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> {
        val conf = DialogConfiguration<W>()
        conf.configure()
        val compJq = jQuery("body")
        val zkComp = findEasyUiComponent(dialogContent)
        compJq.append(zkComp.getHtml())
        val jq = jQuery("#${zkComp.getId()}")
        val result = object:Dialog<W>{
            override fun close() {
                zkComp.destroy()
                jq.dialog("close")
                jq.dialog("destroy")
                jq.remove()
            }

            override fun getContent(): W {
                return dialogContent
            }

        }
        val buttons = conf.buttons.map { db->
            object {
                val text = db.displayName
                val handler = {
                    db.handler.invoke(result)
                }
            }
        }.toTypedArray()
        val dialogConfig = object{
            val title = conf.title
            val modal = true
            val buttons = buttons
        }.asDynamic()
        if(conf.expandToMainFrame){
            val bd = jQuery("body")
            dialogConfig.width = bd.width() - 100
            dialogConfig.height = bd.height() - 100
        }
        zkComp.decorate()
        jq.dialog(dialogConfig)
        if(!conf.expandToMainFrame){
            jq.dialog("resize",object{
                val width = jq.width()+20
//                val width = "auto"
                val height = "auto"
            })
        }
        return result
    }

    override fun createMenuButton(configure: WebMenuButtonConfiguration.() -> Unit): WebMenuButton {
        return EasyUiWebMenuButton(configure)
    }

    override fun createBooleanBox(configure: WebBooleanBoxConfiguration.() -> Unit): WebBooleanBox {
        return EasyUiWebBooleanBox(configure)
    }

    override fun createTableBox(configure: WebTableBoxConfiguration.() -> Unit): WebTableBox {
        return EasyUiWebTableBox(configure)
    }

    override fun createDivsContainer(configure: WebDivsContainerConfiguration.() -> Unit): WebDivsContainer {
        return EasyUiWebDivsContainer(configure)
    }

    override fun createTilesContainer(configure: WebTilesContainerConfiguration.() -> Unit): WebTilesContainer {
        return EasyUiWebTilesContainer(configure)
    }

    override fun createPanel(configure: WebPanelConfiguration.() -> Unit): WebPanel {
        return EasyUiWebPanel(configure)
    }

}



