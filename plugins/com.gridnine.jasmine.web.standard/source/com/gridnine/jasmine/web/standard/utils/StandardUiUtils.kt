/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.utils

import com.gridnine.jasmine.common.core.meta.UiMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.common.standard.rest.MessageDTJS
import com.gridnine.jasmine.common.standard.rest.MessageTypeDTJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.DefaultUIParameters
import com.gridnine.jasmine.web.core.ui.components.NotificationTypeJS
import com.gridnine.jasmine.web.standard.widgets.EnumValueWidget
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.widgets.WebLabelWidget
import kotlin.reflect.KClass

object StandardUiUtils {

    fun hasValidationErrors(vv: BaseVVJS?): Boolean {
        if(vv == null){
            return false
        }
        val description  = UiMetaRegistryJS.get().viewValidations[ReflectionFactoryJS.get().getQualifiedClassName(vv::class)]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id)
            if (value!= null){
                return true
            }
        }
        return false
    }

    fun showError(error: String){
        val msg = MessageDTJS()
        msg.type = MessageTypeDTJS.ERROR
        msg.message = error
        showMessage(msg)
    }

    fun showMessage(message: MessageDTJS?){
        if(message == null){
            return
        }
        WebUiLibraryAdapter.get().showNotification(message.message, when (message.type){
            MessageTypeDTJS.MESSAGE -> NotificationTypeJS.INFO
            MessageTypeDTJS.WARNING -> NotificationTypeJS.WARNING
            MessageTypeDTJS.ERROR -> NotificationTypeJS.ERROR
        },   3000)
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
        val layout = WebGridLayoutWidget {
            uid = "confirmDialog"
        }.also {
            it.setColumnsWidths("auto")
            it.addRow(WebLabelWidget(question))
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
        val widget  = EnumValueWidget<E>{
            width = DefaultUIParameters.controlWidthAsString
            allowNull = false
            enumClass = cls
        }
        val layout = WebGridLayoutWidget {
            uid = "choseDialog"
        }.also {it ->
            it.setColumnsWidths("auto")
            it.addRow(widget)
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