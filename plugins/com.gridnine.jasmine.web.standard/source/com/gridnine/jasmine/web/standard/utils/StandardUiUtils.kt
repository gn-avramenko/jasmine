/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.utils

import com.gridnine.jasmine.common.standard.rest.MessageDTJS
import com.gridnine.jasmine.common.standard.rest.MessageTypeDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.DefaultUIParameters
import com.gridnine.jasmine.web.standard.widgets.EnumValueWidget
import kotlin.reflect.KClass

object StandardUiUtils {

    fun showMessage(message: MessageDTJS?){
        if(message == null){
            return
        }
        val formatedMessage = when (message.type){
            MessageTypeDTJS.MESSAGE -> "<div class=\"notification-message\">${message.message}</div>"
            MessageTypeDTJS.WARNING -> "<div class=\"notification-warning\">${message.message}</div>"
            MessageTypeDTJS.ERROR -> "<div class=\"notification-error\">${message.message}</div>"
        }
        WebUiLibraryAdapter.get().showNotification(formatedMessage, 3000)
    }

    fun showMessage(message: String){
        val msg = MessageDTJS()
        msg.type = MessageTypeDTJS.MESSAGE
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

    fun confirm(question:String, aTitle:String = "Вопрос", action:()->Unit){
        val layout = WebUiLibraryAdapter.get().createGridContainer {
            uid = "confirmDialog"
            column("auto")
            row {
                val label = WebUiLibraryAdapter.get().createLabel {}
                label.setText(question)
                cell(label)
            }
        }

        WebUiLibraryAdapter.get().showDialog(layout){
            title = aTitle
            button {
                displayName = "Да"
                handler = {
                    it.close()
                    action.invoke()
                }
            }
            cancelButton()
        }
    }

    fun<E:Enum<E>> choseVariant(cls:KClass<E>, aTitle:String = "Выберите вариант", action: (E)->Unit){
        lateinit var widget :EnumValueWidget<E>
        val layout = WebUiLibraryAdapter.get().createGridContainer {
            uid = "choseDialog"
            column("auto")
            row {
                widget = EnumValueWidget{
                    width = DefaultUIParameters.controlWidthAsString
                    allowNull = false
                    enumClass = cls
                }
                cell(widget)
            }
        }
        WebUiLibraryAdapter.get().showDialog(layout){
            title = aTitle
            button {
                displayName =  "Да"
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