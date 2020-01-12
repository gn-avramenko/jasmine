/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UNUSED_VARIABLE")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.WorkspaceGroupDTJS
import com.gridnine.jasmine.web.core.model.ui.TextboxDescriptionJS
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTextBoxWidget

class EasyUiWorkspaceGroupEditor : EasyUiWorkspaceElementEditor<WorkspaceGroupDTJS>{


    private lateinit var widget:EasyUiTextBoxWidget

    override fun getContent():String ="<div class=\"jasmine-label\" style=\"display:inline-block;width:70px;position:relative;top:2px\">Название: </div><input id =\"groupName\" style =\"width:300px\">"

    override fun decorate() {
        val description = TextboxDescriptionJS("groupName")
        widget = EasyUiTextBoxWidget("",description)
        widget.configure(Unit)

    }

    override fun setData(data: WorkspaceGroupDTJS) {
        widget.setData(data.displayName)
    }

    override fun getData(): WorkspaceGroupDTJS {
        val result = WorkspaceGroupDTJS()
        result.displayName = widget.getData()
        return result
    }

    override fun validate(): Boolean {
        val res = widget.getData()
        if(res.isNullOrBlank()){
            widget.showValidation("Нужно заполнить название")
            return false
        }
        return true
    }

}