/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.DefaultUIParameters
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.standard.widgets.GeneralSelectWidget
import com.gridnine.jasmine.web.standard.widgets.WebGridCellWidget
import kotlin.reflect.KClass

class WorkspaceListItemVariantHandler:WorkspaceItemVariantHandler<ListWorkspaceItemDTJS,WorkspaceListItemVariantEditor>{
    override fun getModelClass(): KClass<ListWorkspaceItemDTJS> {
        return ListWorkspaceItemDTJS::class
    }
    override fun createEditor(): WorkspaceListItemVariantEditor {
        return WorkspaceListItemVariantEditor()
    }

    override fun setData(editor: WorkspaceListItemVariantEditor, data: ListWorkspaceItemDTJS) {
        editor.value = data
    }

    override fun getData(editor: WorkspaceListItemVariantEditor): ListWorkspaceItemDTJS {
        return editor.value
    }
}

class WorkspaceListItemVariantEditor: BaseWebNodeWrapper<WebBorderContainer>(){
    lateinit var value :ListWorkspaceItemDTJS
    private val listTypeWidget:GeneralSelectWidget
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        listTypeWidget = GeneralSelectWidget {
            width = DefaultUIParameters.controlWidthAsString
        }

        val cellWidget = WebGridCellWidget("Тип объекта", listTypeWidget)
        _node.setNorthRegion {
            content = cellWidget
        }
    }
}