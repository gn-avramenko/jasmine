/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.utils

import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.widgets.ServerUiEnumValueWidget
import kotlin.reflect.KClass

object ServerUiUtils {
    fun confirm(question:String, dialogTitle:String = "Вопрос", action:()->Unit){
        val label = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        label.setText(question)
        ServerUiLibraryAdapter.get().showDialog(ServerUiDialogConfiguration {
            title = dialogTitle
            editor = label
            button {
                displayName = "Да"
                handler = {
                    it.close()
                    action.invoke()
                }
            }
            cancelButton()
        })
    }

    fun<E:Enum<E>> choseVariant(cls: KClass<E>, aTitle:String = "Выберите вариант", action: (E)->Unit){
        val layout = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        layout.addRow()
        val widget = ServerUiEnumValueWidget<E>{
            width = "200px"
            allowNull = false
            enumClass = cls
        }
        layout.addCell(ServerUiGridLayoutCell(widget))
        ServerUiLibraryAdapter.get().showDialog(ServerUiDialogConfiguration{
            title = aTitle
            editor = layout
            button {
                displayName = "Да"
                handler = {
                    val selectedValue = widget.getValue()!!
                    it.close()
                    action.invoke(selectedValue)
                }
            }
            cancelButton()
        })
    }
}