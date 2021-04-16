/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.tools

import com.gridnine.jasmine.common.core.model.BaseVM
import com.gridnine.jasmine.common.core.model.BaseVS
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.UiEditorHelper
import com.gridnine.jasmine.server.core.ui.common.ViewEditor
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import com.gridnine.jasmine.server.standard.ui.mainframe.ObjectEditor
import com.gridnine.jasmine.server.standard.ui.mainframe.ObjectEditorButton

@Suppress("UNCHECKED_CAST")
class SaveObjectEditorButton : ObjectEditorButton<BaseVM, ViewEditor<BaseVM, *,*>>{
    override fun isApplicable(vm: BaseVM, editor: ObjectEditor<ViewEditor<BaseVM, *, *>>): Boolean {
        return !editor.readOnly
    }

    override fun onClick(value: ObjectEditor<ViewEditor<BaseVM, *, *>>) {
        value as ObjectEditor<ViewEditor<BaseVM, BaseVS, BaseVV>>
        val vm = value.rootEditor.getData()
        val objectId = value.reference.type.qualifiedName!!
        val objectUid =value.reference.uid
        val result = UiEditorHelper.saveEditorData(objectId, objectUid, vm)
        val validation = result.vv
        value.rootEditor.showValidation(validation)
        if(validation != null && UiUtils.hasValidationErrors(validation)){
            UiUtils.showError(StandardL10nMessagesFactory.Validation_errors_exist())
            return
        }
        value.rootEditor.setData(result.vm!!, result.vs)
        value.updateTitle(result.title)
        if(result.newUid != null){
            value.reference.uid = result.newUid!!
        }
        UiUtils.showInfo(StandardL10nMessagesFactory.Data_saved())
    }

    override fun getDisplayName(): String {
        return StandardL10nMessagesFactory.save()
    }

    override fun getId(): String {
        return this::javaClass.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}