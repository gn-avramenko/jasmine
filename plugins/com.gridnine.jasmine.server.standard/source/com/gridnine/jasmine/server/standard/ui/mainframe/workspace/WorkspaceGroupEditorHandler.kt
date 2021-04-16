/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.common.standard.model.workspace.WorkspaceGroupEditor
import com.gridnine.jasmine.common.standard.model.workspace.WorkspaceGroupEditorVM
import java.util.*

class WorkspaceGroupEditorHandler : WorkspaceElementEditorHandler<WorkspaceGroupEditor,WorkspaceGroup>{
    private val uid = UUID.randomUUID().toString()
    override fun getId(): String {
        return uid
    }

    override fun createEditor(): WorkspaceGroupEditor {
        return WorkspaceGroupEditor()
    }

    override fun setData(editor: WorkspaceGroupEditor, data: WorkspaceGroup) {
        val vm = WorkspaceGroupEditorVM()
        vm.name = data.displayName
        vm.uid = data.uid
        editor.setData(vm,null)
    }

    override fun getData(editor: WorkspaceGroupEditor): WorkspaceGroup {
        val result = WorkspaceGroup()
        val data = editor.getData()
        result.uid =data.uid!!
        result.displayName = data.name
        return result
    }

    override fun validate(editor: WorkspaceGroupEditor): Boolean {
        if(editor.nameWidget.getValue() == null){
            editor.nameWidget.showValidation(StandardL10nMessagesFactory.Empty_field())
            return false
        }
        return true
    }

    override fun getName(data: WorkspaceGroup): String {
        return data.displayName?:"???"
    }

}

