/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.Dialog
import com.gridnine.jasmine.server.core.ui.components.DialogConfiguration
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button
import org.zkoss.zul.Hbox
import org.zkoss.zul.Window

fun<W> zkShowDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> where W: UiNode{
    val config = DialogConfiguration<W>()
    config.configure()
    val comp = Executions.getCurrent().desktop.pages.iterator().next().firstRoot
    val existingDialog = comp.getChildren<Component>().find { it.id == "jasmine-dialog" }
    existingDialog?.parent?.removeChild(existingDialog)
    val dialog  = Window()
    dialog.parent = comp
    val result = object: Dialog<W>{
        override fun close() {
            dialog.onClose()
        }

        override fun getContent(): W {
            return dialogContent
        }

    }
    dialog.mode ="modal"
    dialog.isClosable = false
    dialog.isMinimizable = false
    dialog.isMaximizable = false
    dialog.title = config.title
    dialog.appendChild(findZkComponent(dialogContent).getZkComponent())
    val box = Hbox()
    box.width = "100%"
    box.style = "padding:5px"
    box.pack = "end"
    box.parent = dialog
    config.buttons.forEach { buttonConfig ->
        val button = Button()
        button.label = buttonConfig.displayName
        button.addEventListener(Events.ON_CLICK){
            buttonConfig.handler.invoke(result)
        }
        button.parent = box
    }
    dialog.doModal()
    return result
}
