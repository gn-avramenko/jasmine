/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.Dialog
import com.gridnine.jasmine.web.core.ui.DialogButtonHandler
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.mainframe.EasyUiMainFrameImpl
import com.gridnine.jasmine.web.easyui.utils.EasyUiViewBuilder

class EasyUiFactory:UiFactory{
    override fun <VM : BaseVMEntityJS, VS : BaseVSEntityJS, VV : BaseVVEntityJS, V : BaseView<VM, VS, VV>,D:Dialog<VM,VS,VV,V>> showDialog(dialog:D, model: VM, settings: VS): D{
        val dialogId = ReflectionFactoryJS.get().getQualifiedClassName(dialog::class)
        val dialogDescription = UiMetaRegistryJS.get().dialogs[dialogId]?:throw IllegalArgumentException("unable to find dialog description for $dialogId")
        val dialogDiv = jQuery("#dialog")
        if(dialogDiv.length > 0){
            dialogDiv.remove()
        }
        val dialogContent = HtmlUtilsJS.html {
            div(id = "dialog", style = "display:None"){
               EasyUiViewBuilder.generateHtml(dialogDescription.viewId,"",expandToParent = false, builder = this)
            }
        }.toString()
        jQuery("body").append(dialogContent)
        val buttonsArr = arrayOfNulls<Any>(dialogDescription.buttons.size)
        dialogDescription.buttons.withIndex().forEach { (idx, elm) ->
            val widget = DialogButtonWidget()
            widget.id= elm.id
            dialog.buttons.add(widget)
            buttonsArr[idx] = object {
                val buttonHandler =ReflectionFactoryJS.get().getFactory(elm.handler).invoke()
                val text = elm.displayName
                val handler = {
                    buttonHandler.asDynamic().handle(dialog)
                }
            }
        }
        jQuery("#dialog").dialog(object{
            val title = dialogDescription.title
            val closed= false
            val cache = false
            val modal = true
            val closable = dialogDescription.closable
            val buttons = buttonsArr
        })
        val view = EasyUiViewBuilder.createView<VM,VS,VV,V>(dialogDescription.viewId, "")
        view.configure(settings)
        view.readData(model)

        dialog.view = view
        dialog.close = {jQuery("#dialog").dialog("close")}
        return dialog
    }

    override fun publishMainFrame() {
        if(EnvironmentJS.isPublished(MainFrame::class)){
            return
        }
        EnvironmentJS.publish(MainFrame::class, EasyUiMainFrameImpl())
    }

    override fun showConfirmDialog(question: String, handler: () -> Unit) {
        confirm(question, handler)
    }

    override fun showNotification(message: String, title: String?, timeout: Int) {
        showMessage(title, message, timeout)
    }

    override fun showLoader() {
        com.gridnine.jasmine.web.easyui.showLoader()
    }

    override fun hideLoader() {
        com.gridnine.jasmine.web.easyui.hideLoader()
    }
}
