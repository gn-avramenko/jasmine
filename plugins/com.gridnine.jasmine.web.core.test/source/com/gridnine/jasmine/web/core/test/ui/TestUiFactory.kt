package com.gridnine.jasmine.web.core.test.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.Dialog
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.ui.TestableDialogButtonHandler
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS

class TestUiFactory : UiFactory {
    override fun <VM : BaseVMEntityJS, VS : BaseVSEntityJS, VV : BaseVVEntityJS, V : BaseView<VM, VS, VV>, D : Dialog<VM, VS, VV, V>> showDialog(dialog: D, model: VM, settings: VS): D {
        val dialogId = ReflectionFactoryJS.get().getQualifiedClassName(dialog::class)
        val dialogDescription = UiMetaRegistryJS.get().dialogs[dialogId]
                ?: throw IllegalArgumentException("unable to find dialog description for $dialogId")


        val view = TestViewBuilder.createView<VM, VS, VV, V>(dialogDescription.viewId, "")
        view.configure(settings)
        view.readData(model)
        dialog.view = view
        dialog.close = {}
        dialogDescription.buttons.withIndex().forEach { (idx, elm) ->
            val buttonHandler = ReflectionFactoryJS.get().getFactory(elm.handler).invoke()
            if (buttonHandler is TestableDialogButtonHandler<*, *, *, *, *>) {
                val button = TestableDialogButtonWidget<Any>()
                button.id = elm.id
                button.click = {
                    buttonHandler.handle(dialog.asDynamic()).asDynamic()
                }
                dialog.buttons.add(button)
            } else {
                val button = DialogButtonWidget()
                button.id = elm.id
                dialog.buttons.add(button)
            }
        }
        return dialog
    }

    override fun publishMainFrame() {
        EnvironmentJS.publish(MainFrame::class, TestMainFrame())
    }

    override fun showConfirmDialog(question: String, handler: () -> Unit) {
        handler()
    }

    override fun showNotification(message: String, title: String?, timeout: Int) {
        console.log(message)
    }

}