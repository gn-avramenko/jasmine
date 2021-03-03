/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.utils

import com.gridnine.jasmine.web.server.components.ServerUiDialogConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiLabelConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter

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
}