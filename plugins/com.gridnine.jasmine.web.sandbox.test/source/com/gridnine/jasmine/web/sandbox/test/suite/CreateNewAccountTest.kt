package com.gridnine.jasmine.web.sandbox.test.suite

import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.web.core.model.ui.Editor
import com.gridnine.jasmine.web.core.model.ui.TestableDialogButtonWidget
import com.gridnine.jasmine.web.core.model.ui.TestableSharedEditorToolButtonHandler
import com.gridnine.jasmine.web.core.model.ui.TestableToolButtonWidget
import com.gridnine.jasmine.web.core.model.ui.widgets.ProxyTextBoxWidget
import com.gridnine.jasmine.web.core.test.ext.Assert
import com.gridnine.jasmine.web.core.test.ext.describe
import com.gridnine.jasmine.web.core.test.ext.it
import com.gridnine.jasmine.web.core.ui.UiFactory
import kotlin.js.Promise

class CreateNewAccountTest {
    fun createNewAccountTest() {
        val assert = com.gridnine.jasmine.web.core.test.ext.require("assert") as Assert
        describe("work-with-account") {
            it("test-create-new-account") {
                val dialog = UiFactory.get().showDialog(SandboxCreateUserAccountDialog(), SandboxCreateUserAccountDialogVMJS(), SandboxCreateUserAccountDialogVSJS())
                dialog.view.login.setData("user")
                dialog.view.password.setData("user")
                dialog.view.passwordRepeat.setData("user")
                (dialog.buttons.find { it.id == "createButton" } as TestableDialogButtonWidget<Editor<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>>).click()
                        .then {
                            assert.equal(it.view.login.getData(), "user")
                            it.view.name.setData(null)
                            val widget = it.toolButtons.find { it.id == "sharedSaveEditorButton" } as TestableToolButtonWidget<Editor<SandboxUserAccountEditorVMJS, SandboxUserAccountEditorVSJS, SandboxUserAccountEditorVVJS, SandboxUserAccountEditorView>>
                            widget.click()
                        }.then { it ->
                            val validation = (it.view.name as ProxyTextBoxWidget).getValidationValue()
                            assert.ok(validation != null)
                        }
            }
        }
    }
}