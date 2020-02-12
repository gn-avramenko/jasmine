/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.shared

import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.BaseIndexJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.UiFactory


class SandboxSharedListDeleteButtonHandler: SharedListToolButtonHandler<BaseEntityJS>{
    override fun isVisible(list: EntityList<BaseEntityJS>): Boolean {
        return true
    }

    override fun isEnabled(list: EntityList<BaseEntityJS>): Boolean {
        return list.getSelectedElements().isNotEmpty()
    }

    override fun isApplicableToList(listId: String): Boolean {
        return true
    }

    override fun onClick(list: EntityList<BaseEntityJS>) {
        val selectedLst = list.getSelectedElements()
        if(selectedLst.isEmpty()){
            return
        }
        val selected = selectedLst[0]
        val title = if(selected is BaseIndexJS) selected.document.caption else "объект"
        UiFactory.get().showConfirmDialog("Вы действительно хотите удалить ${title}?") {
            val request = DeleteObjectRequestJS()
            request.objectId = if(selected is BaseIndexJS) selected.document.type else list.getListId().substringBeforeLast("JS")
            request.objectUid = if(selected is BaseIndexJS) selected.document.uid!! else selected.uid!!
            StandardRestClient.standard_standard_deleteObject(request).then {
                list.reload()
                UiFactory.get().showNotification("Объект удален") }
        }
    }

}