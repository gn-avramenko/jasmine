/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.tools

import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.standard.helpers.UiEditorHelper
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiNotificationType
import com.gridnine.jasmine.web.server.components.ServerUiViewEditor
import com.gridnine.jasmine.web.server.mainframe.ServerUiObjectEditor
import com.gridnine.jasmine.web.server.mainframe.ServerUiObjectEditorButton

class ServerUiSaveObjectEditorButton : ServerUiObjectEditorButton<BaseVM, ServerUiViewEditor<BaseVM, *,*>>{
    override fun isApplicable(vm: BaseVM, editor: ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>): Boolean {
        return !editor.readOnly
    }

    override fun onClick(value: ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>) {
        value as ServerUiObjectEditor<ServerUiViewEditor<BaseVM, BaseVS, BaseVV>>
        val vm = value.rootEditor.getData()
        val objectId = value.reference.type.qualifiedName!!
        val objectUid =value.reference.uid
        val result = UiEditorHelper.saveEditorData(objectId, objectUid, vm)
        val validation = result.vv
        value.rootEditor.showValidation(validation)
        if(validation != null && ValidationUtils.hasValidationErrors(validation)){
            ServerUiLibraryAdapter.get().showNotification("Есть ошибки валидации", ServerUiNotificationType.ERROR, 3000);
            return
        }
        value.rootEditor.setData(result.vm!!, result.vs)
        value.updateTitle(result.title)
        if(result.newUid != null){
            value.reference.uid = result.newUid!!
        }
        ServerUiLibraryAdapter.get().showNotification("Данные сохранены", ServerUiNotificationType.INFO, 2000);
    }

    override fun getDisplayName(): String {
        return "Сохранить"
    }

    override fun getId(): String {
        return this::javaClass.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}