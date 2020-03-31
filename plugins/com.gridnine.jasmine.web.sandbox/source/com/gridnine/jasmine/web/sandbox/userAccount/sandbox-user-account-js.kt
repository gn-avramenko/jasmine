/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.userAccount

import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndexJS
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxCreateUserAccountRequestJS
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxUpdatePasswordRequestJS
import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.web.core.model.ui.BaseEditorToolButtonHandler
import com.gridnine.jasmine.web.core.model.ui.BaseListToolButtonHandler
import com.gridnine.jasmine.web.core.model.ui.Editor
import com.gridnine.jasmine.web.core.model.ui.EntityList
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.sandbox.SandboxRestClient

class SandboxCreateUserAccountListToolButtonHandler : BaseListToolButtonHandler<SandboxUserAccountIndexJS>{
    override fun isVisible(list: EntityList<SandboxUserAccountIndexJS>): Boolean {
        return true;
    }

    override fun isEnabled(list: EntityList<SandboxUserAccountIndexJS>): Boolean {
        return true;
    }

    override fun onClick(list: EntityList<SandboxUserAccountIndexJS>) {
        UiFactory.get().showDialog(SandboxCreateUserAccountDialog(), SandboxCreateUserAccountDialogVMJS(), SandboxCreateUserAccountDialogVSJS())
    }

}

class SandboxCreateUserAccountDialogButtonHandler:DialogButtonHandler<SandboxCreateUserAccountDialogVMJS, SandboxCreateUserAccountDialogVSJS, SandboxCreateUserAccountDialogVVJS,SandboxCreateUserAccountDialogView>{
    override fun handle(dialog: Dialog<SandboxCreateUserAccountDialogVMJS, SandboxCreateUserAccountDialogVSJS, SandboxCreateUserAccountDialogVVJS, SandboxCreateUserAccountDialogView>) {
        val model = SandboxCreateUserAccountDialogVMJS()
        dialog.view.writeData(model)
        val request = SandboxCreateUserAccountRequestJS()
        request.model = model
        SandboxRestClient.sandbox_userAccount_createAccount(request).then {
            if(ValidationUtilsJS.hasValidationErrors(it.validation)){
                dialog.view.showValidation(it.validation)
                return@then
            }
            dialog.close()
            MainFrame.get().openTab(it.result!!.type, it.result!!.uid, null)
        }
    }

}
class SandboxUpdatePasswordEditorToolButtonHandler : BaseEditorToolButtonHandler<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>{
    override fun onClick(editor: Editor<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>) {
        val dialog = UiFactory.get().showDialog(SandboxUpdatePasswordDialog(), SandboxUpdatePasswordDialogVMJS(), SandboxUpdatePasswordDialogVSJS())
        dialog.editorView = editor.view
    }

    override fun isVisible(editor: Editor<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>): Boolean {
        return true
    }

    override fun isEnabled(editor: Editor<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>): Boolean {
        return true
    }
}

class SandboxUpdatePasswordDialogButtonHandler:DialogButtonHandler<SandboxUpdatePasswordDialogVMJS, SandboxUpdatePasswordDialogVSJS,SandboxUpdatePasswordDialogVVJS, SandboxUpdatePasswordDialogView>{
    override fun handle(dialog: Dialog<SandboxUpdatePasswordDialogVMJS, SandboxUpdatePasswordDialogVSJS, SandboxUpdatePasswordDialogVVJS, SandboxUpdatePasswordDialogView>) {
        val editorView = dialog.editorView as SandboxUserAccountEditorView
        val model = SandboxUpdatePasswordDialogVMJS()
        dialog.view.writeData(model)
        val request = SandboxUpdatePasswordRequestJS()
        request.login = editorView.login.getData()
        request.model = model
        SandboxRestClient.sandbox_userAccount_updatePassword(request).then {
            if(ValidationUtilsJS.hasValidationErrors(it.validation)){
                dialog.view.showValidation(it.validation)
                return@then
            }
            dialog.close()
            UiFactory.get().showNotification("Пароль успешно обновлен")
        }

    }

}