/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.Dialog
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS

class EasyUiFactory:UiFactory{
    override fun <VM : BaseVMEntityJS, VS : BaseVSEntityJS, VV : BaseVVEntityJS, V : BaseView<VM, VS, VV>> showDialog(dialogId: String, model: VM, settings: VS): Dialog<VM, VS, VV, V> {
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
            buttonsArr[idx] = object {
                val text = elm.displayName
                val handler = {
                    console.log("pressed $text")
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
        view.readData(model, settings)
        val dialog = Dialog<VM, VS, VV, V>();
        dialog.view = view
        return dialog
    }

}
