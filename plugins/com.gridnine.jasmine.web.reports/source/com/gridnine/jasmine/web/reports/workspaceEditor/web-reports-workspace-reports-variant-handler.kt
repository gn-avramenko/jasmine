/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.workspaceEditor

import com.gridnine.jasmine.common.reports.model.domain.ReportDescriptionIndexJS
import com.gridnine.jasmine.common.reports.model.rest.ReportsWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.workspaceEditor.WorkspaceItemVariantHandler
import kotlin.reflect.KClass

class WorkspaceReportsItemVariantHandler:WorkspaceItemVariantHandler<ReportsWorkspaceItemDTJS,WorkspaceReportsItemVariantEditor>{
    override fun getModelClass(): KClass<ReportsWorkspaceItemDTJS> {
        return ReportsWorkspaceItemDTJS::class
    }
    override fun createEditor(): WorkspaceReportsItemVariantEditor {
        return WorkspaceReportsItemVariantEditor()
    }

    override fun setData(editor: WorkspaceReportsItemVariantEditor, data: ReportsWorkspaceItemDTJS) {
        editor.value = data
    }

    override fun getData(editor: WorkspaceReportsItemVariantEditor): ReportsWorkspaceItemDTJS {
        return editor.value
    }
}

class WorkspaceReportsItemVariantEditor: BaseWebNodeWrapper<WebGridLayoutWidget>(){
    var value :ReportsWorkspaceItemDTJS
    init {
        _node = WebGridLayoutWidget{}
        value = ReportsWorkspaceItemDTJS().apply {
            listId = ReportDescriptionIndexJS.indexId.substringBeforeLast("JS")
            columns.add("name")
        }
    }
}