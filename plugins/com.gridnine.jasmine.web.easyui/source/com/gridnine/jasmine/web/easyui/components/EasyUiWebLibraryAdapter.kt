/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.common.core.model.XeptionTypeJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window

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

    override fun createSearchBox(configure: WebSearchBoxConfiguration.() -> Unit):WebSearchBox {
        return EasyUiWebSearchBox(configure)
    }

    override fun <E : BaseIntrospectableObjectJS> createDataGrid(configure: WebDataGridConfiguration<E>.()->Unit): WebDataGrid<E> {
        return EasyUiWebDataGrid(configure)
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
        val config = WebSelectConfiguration()
        config.configure()
        if(config.multiple){
            return EasyUiWebTagbox(config)
        }
        return EasyUiWebCombobox(config)
    }

    override fun createTextBox(configure: WebTextBoxConfiguration.() -> Unit): WebTextBox {
        return EasyUiWebTextBox(configure)
    }

    override fun createPasswordBox(configure: WebPasswordBoxConfiguration.() -> Unit): WebPasswordBox {
        return EasyUiWebPasswordBox(configure)
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

    override fun showNotification(message: String, notificationType:NotificationTypeJS, timeout: Int) {
        val formatedMessage = when (notificationType){
            NotificationTypeJS.INFO -> "<div class=\"notification-message\">${message}</div>"
            NotificationTypeJS.WARNING -> "<div class=\"notification-warning\">${message}</div>"
            NotificationTypeJS.ERROR -> "<div class=\"notification-error\">${message}</div>"
        }
        jQuery.messager.show(object{
            val msg = message
            val timeout = timeout
            val showType = "show"
        })
    }


    override fun <W : WebNode> showDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> {
        val conf = DialogConfiguration<W>()
        conf.configure()
        if(EnvironmentJS.test){
            val holder = arrayListOf<Dialog<W>>()
            val result = object:Dialog<W>{
                override fun close() {
                    //noops
                }

                override fun getContent(): W {
                    return dialogContent
                }

                override suspend fun simulateClick(buttonIdx: Int): Any? {
                    return conf.buttons[buttonIdx].handler.invoke(holder[0])
                }
            }
            holder.add(result)
            return result
        }
        val compJq = jQuery("body")
        val zkComp = findEasyUiComponent(dialogContent)
        if(zkComp.getId() == null){
            throw XeptionJS.forDeveloper("dialog component must have and id")
        }
        compJq.append(zkComp.getHtml())
        val jq = jQuery("#${zkComp.getId()}")
        val holder = arrayListOf<Dialog<W>>()
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

            override suspend fun simulateClick(buttonIdx: Int): Any? {
                return conf.buttons[buttonIdx].handler.invoke(holder[0])
            }

        }
        holder.add(result)
        val buttons = conf.buttons.map { db->
            object {
                val text = db.displayName
                val handler = {
                    launch {
                        db.handler.invoke(result)
                    }
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
                val width = jq.width()+30
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

    override fun createPanel(configure: WebPanelConfiguration.() -> Unit): WebPanel {
        return EasyUiWebPanel(configure)
    }



    override fun createAccordionContainer(configure: WebAccordionContainerConfiguration.() -> Unit):WebAccordionContainer {
        return EasyUiWebAccordionContainer(configure)
    }

    override fun createTag(tagName:String, id: String?): WebTag {
        return EasyUiWebTag(tagName, id)
    }

    override fun createRichTextEditor(configure: WebRichTextEditorConfiguration.() -> Unit): WebRichTextEditor {
        return EasyUiWebRTEditor(configure)
    }



}



