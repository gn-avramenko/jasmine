/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.UiUtils
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

    override fun createGridLayoutContainer(parent: WebComponent?, configure: WebGridLayoutContainerConfiguration.() -> Unit): WebGridLayoutContainer {
        return EasyUiWebGridLayoutContainer(parent, configure)
    }

    override fun <E : BaseIntrospectableObjectJS> createDataGrid(parent: WebComponent?, configure: WebDataGridConfiguration<E>.() -> Unit): WebDataGrid<E> {
        return EasyUiWebDataGrid(parent, configure)
    }

    override fun createSearchBox(parent: WebComponent?, configure: WebSearchBoxConfiguration.() -> Unit): WebSearchBox {
        return EasyUiWebSearchBox(parent, configure)
    }

    override fun createTextBox(parent: WebComponent?, configure: WebTextBoxConfiguration.() -> Unit): WebTextBox {
        return EasyUiWebTextBox(parent, configure)
    }

    override fun createPasswordBox(parent: WebComponent?, configure: WebPasswordBoxConfiguration.() -> Unit): WebPasswordBox {
        return EasyUiWebPasswordBox(parent,configure)
    }

    override fun createLinkButton(parent: WebComponent?, configure: WebLinkButtonConfiguration.() -> Unit): WebLinkButton {
        return EasyUiWebLinkButton(parent, configure)
    }


    override fun createDateBox(parent: WebComponent?, configure: WebDateBoxConfiguration.() -> Unit): WebDateBox {
        return EasyUiWebDateBox(parent, configure)
    }

    override fun createDateTimeBox(parent: WebComponent?, configure: WebDateTimeBoxConfiguration.() -> Unit): WebDateTimeBox {
        return EasyUiWebDateTimeBox(parent, configure)
    }

    override fun createNumberBox(parent: WebComponent?, configure: WebNumberBoxConfiguration.() -> Unit): WebNumberBox {
        return EasyUiWebNumberBox(parent, configure)
    }


    override fun createSelect(parent: WebComponent, configure: WebSelectConfiguration.() -> Unit): WebSelect {
        return EasyUiWebSelect(parent, configure)
    }

    override fun <W : WebEditor<*, *, *>> showDialog(popupChild: WebComponent, configure: DialogConfiguration<W>.() -> Unit): WebDialog<W> where W : HasDivId {
        val conf = DialogConfiguration<W>(configure)
        val comp = UiUtils.findParent(popupChild,WebPopupContainer::class)?:throw XeptionJS.forDeveloper("unable to find popup container")
        jQuery("#${comp.getId()}").append(conf.editor.getHtml())
        val jq = jQuery("#${conf.editor.getId()}")
        val result = object:WebDialog<W>{
            override fun close() {
                conf.editor.destroy()
                jq.dialog("close")
                jq.dialog("destroy")
                jq.remove()
            }

            override fun getEditor(): W {
                return conf.editor
            }

        }
        val butttons = conf.buttons.map {db->
            object {
                val text = db.displayName
                val handler = {
                    db.handler.invoke(result)
                }
            }
        }.toTypedArray()
        jq.dialog(object{
            val title = conf.title
            val modal = true
            val buttons = butttons
        });
        conf.editor.decorate()
        return result
    }

    override fun createMenuButton(parent: WebComponent?, configure: WebMenuButtonConfiguration.() -> Unit): WebMenuButton {
        return EasyUiWebMenuButton(parent, configure)
    }

    override fun showLoader() {
        jQuery.messager.progress()


    }

    override fun hideLoader() {
        jQuery.messager.progress("close")
    }

    override fun showNotification(message: String, timeout: Int) {
        jQuery.messager.show(object{
            val msg = message
            val timeout = timeout
            val showType = "show"
        });
    }


}
external fun createSelect2Option(id:String, text:String?, defaultSelected: Boolean, selected:Boolean)
external var jQuery: dynamic = definedExternally
