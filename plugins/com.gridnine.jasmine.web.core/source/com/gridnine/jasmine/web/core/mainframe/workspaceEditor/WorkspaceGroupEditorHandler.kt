/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.WorkspaceGroupJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditor
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditorVMJS
import com.gridnine.jasmine.web.core.workspace.WorkspaceGroupEditorVSJS

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

