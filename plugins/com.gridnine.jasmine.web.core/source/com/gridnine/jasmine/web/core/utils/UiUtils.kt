/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.server.standard.rest.MessageJS
import com.gridnine.jasmine.server.standard.rest.MessageTypeJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.ui.DefaultUIParameters
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebLabel
import com.gridnine.jasmine.web.core.ui.widgets.EnumValueWidget
import com.gridnine.jasmine.web.core.ui.widgets.GridCellWidget
import kotlin.reflect.KClass

object UiUtils {
    fun<W:WebComponent> findParent(child:WebComponent, cls:KClass<W>):W?{
        if(cls.isInstance(child)){
            return child as W
        }
        if(child.getParent() == null){
            return null
        }
        return findParent(child.getParent()!!, cls)
    }

    fun showMessage(message: MessageJS?){
        if(message == null){
            return
        }
        val formatedMessage = when (message.type){
            MessageTypeJS.MESSAGE -> "<div class=\"notification-message\">${message.message}</div>"
            MessageTypeJS.WARNING -> "<div class=\"notification-warning\">${message.message}</div>"
            MessageTypeJS.ERROR -> "<div class=\"notification-error\">${message.message}</div>"
        }
        UiLibraryAdapter.get().showNotification(formatedMessage, 3000)
    }

    fun showMessage(message: String){
        val msg = MessageJS()
        msg.type = MessageTypeJS.MESSAGE
        msg.message = message
        showMessage(msg)
    }

    fun replaceMessageParameters(message:String, vararg params:Any?):String{
        var result = message
        params.withIndex().forEach{(idx, value) ->
            result = result.replace("{$idx}", value.toString())
        }
        return result
    }

    fun confirm(question:String, aTitle:String = CoreWebMessagesJS.question, action:()->Unit){
        val layout = UiLibraryAdapter.get().createGridLayoutContainer(null){
            uid = "confirmDialog"
        }
        layout.defineColumn("auto")
        layout.addRow()
        val label = UiLibraryAdapter.get().createLabel(layout)
        label.setText(question)
        layout.addCell(WebGridLayoutCell(label))

        UiLibraryAdapter.get().showDialog<WebGridLayoutContainer>(null){
            title = aTitle
            editor = layout
            button {
                displayName = CoreWebMessagesJS.YES
                handler = {
                    it.close()
                    action.invoke()
                }
            }
            cancelButton()
        }
    }

    fun<E:Enum<E>> choseVariant(cls:KClass<E>, aTitle:String = CoreWebMessagesJS.ChoseVariant, action: (E)->Unit){
        val layout = UiLibraryAdapter.get().createGridLayoutContainer(null){
            uid = "choseDialog"
        }
        layout.defineColumn("auto")
        layout.addRow()
        val widget = EnumValueWidget<E>(layout){
            width = DefaultUIParameters.controlWidthAsString
            allowNull = false
            enumClass = cls
        }
        layout.addCell(WebGridLayoutCell(widget))
        UiLibraryAdapter.get().showDialog<WebGridLayoutContainer>(null){
            title = aTitle
            editor = layout
            button {
                displayName = CoreWebMessagesJS.YES
                handler = {
                    val selectedValue = widget.getValue()!!
                    it.close()
                    action.invoke(selectedValue)
                }
            }
            cancelButton()
        }
    }
}