/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceGroupJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.widgets.GridCellWidget
import com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditorVMJS
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditorVSJS
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditorVVJS

class WorkspaceGroupEditorHandler : WorkspaceElementEditorHandler<WorkspaceGroupEditor,WorkspaceGroupJS>{
    override fun getId(): String {
        return "workspace-group-editor"
    }

    override fun createEditor(parent:WebComponent): WorkspaceGroupEditor {
        return WorkspaceGroupEditor(parent)
    }

    override fun setData(editor: WorkspaceGroupEditor, data: WorkspaceGroupJS) {
        val vm = WorkspaceGroupEditorVMJS()
        vm.name = data.displayName
        vm.uid = data.uid
        val vs = WorkspaceGroupEditorVSJS()
        editor.readData(vm,vs)
    }

    override fun getData(editor: WorkspaceGroupEditor): WorkspaceGroupJS {
        val result = WorkspaceGroupJS()
        val data = editor.getData()
        result.uid =data.uid!!
        result.displayName = data.name
        return result
    }

    override fun validate(editor: WorkspaceGroupEditor): Boolean {
        if(editor.nameWidget.getValue() == null){
            editor.nameWidget.showValidation("Поле должно быть заполнено")
            return false
        }
        return true
    }

    override fun getName(data: WorkspaceGroupJS): String {
        return data.displayName?:"???"
    }

}

class WorkspaceGroupEditor(private val parent: WebComponent?): WebEditor<WorkspaceGroupEditorVMJS, WorkspaceGroupEditorVSJS, WorkspaceGroupEditorVVJS>, HasDivId {

    private val delegate: WebGridLayoutContainer

    val nameWidget: TextBoxWidget

    var uid:String? = null

    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this) {}
        delegate.defineColumn(DefaultUIParameters.controlWidthAsString)
        delegate.addRow()
        val nameCell = GridCellWidget(delegate, L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditor"]?.get("name")
                ?: "???") { par ->
            TextBoxWidget(par) {
                width = "100%"
            }
        }
        delegate.addCell(WebGridLayoutCell(nameCell))
        nameWidget = nameCell.widget
    }

    override fun readData(vm: WorkspaceGroupEditorVMJS, vs: WorkspaceGroupEditorVSJS) {
        nameWidget.setValue(vm.name)
        uid = vm.uid
        vs.name?.let { nameWidget.configure(it) }
    }

    override fun setReadonly(value: Boolean) {
        nameWidget.setReadonly(value)
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun destroy() {
        delegate.destroy()
    }

    override fun getData(): WorkspaceGroupEditorVMJS {
        val result = WorkspaceGroupEditorVMJS()
        result.name = nameWidget.getValue()
        result.uid = uid
        return result
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun showValidation(validation: WorkspaceGroupEditorVVJS) {
        validation.name?.let { nameWidget.showValidation(it) }
    }

    override fun getId(): String {
        return delegate.getId()
    }

}