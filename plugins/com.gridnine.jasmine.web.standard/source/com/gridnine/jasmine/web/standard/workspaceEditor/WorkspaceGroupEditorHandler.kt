/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.rest.WorkspaceGroupDTJS
import com.gridnine.jasmine.common.standard.model.workspace.WorkspaceGroupEditor
import com.gridnine.jasmine.common.standard.model.workspace.WorkspaceGroupEditorVMJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class WorkspaceGroupEditorHandler : WorkspaceElementEditorHandler<WorkspaceGroupEditor,WorkspaceGroupDTJS>{
    override fun getId(): String {
        return "workspace-group-editor"
    }

    override fun createEditor(): WorkspaceGroupEditor {
        return WorkspaceGroupEditor()
    }

    override fun setData(editor: WorkspaceGroupEditor, data: WorkspaceGroupDTJS) {
        val vm = WorkspaceGroupEditorVMJS()
        vm.name = data.displayName
        vm.uid = data.uid
        editor.readData(vm,null)
    }

    override fun getData(editor: WorkspaceGroupEditor): WorkspaceGroupDTJS {
        val result = WorkspaceGroupDTJS()
        val data = editor.getData()
        result.uid =data.uid!!
        result.displayName = data.name
        return result
    }

    override fun validate(editor: WorkspaceGroupEditor): Boolean {
        if(MiscUtilsJS.isBlank(editor.nameWidget.getValue())){
            editor.nameWidget.showValidation("Поле должно быть заполнено")
            return false
        }
        return true
    }

    override fun getName(data: WorkspaceGroupDTJS): String {
        return data.displayName?:"???"
    }

}

