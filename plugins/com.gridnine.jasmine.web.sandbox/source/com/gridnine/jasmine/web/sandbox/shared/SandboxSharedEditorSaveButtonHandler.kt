/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.shared

import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.core.ui.ValidationUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS


class SandboxSharedEditorSaveButtonHandler : SharedEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>> {
    override fun onClick(editor: Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>) {
        val objectId = editor.type
        val editorDescr = UiMetaRegistryJS.get().editors.values.find { it.entityId == objectId }?:throw IllegalArgumentException("unable to find editor description for $objectId")
        val viewDescr  = UiMetaRegistryJS.get().views[editorDescr.viewId]?:throw IllegalArgumentException("unable to find view for ${editorDescr.viewId}")
        val viewModel = ReflectionFactoryJS.get().getFactory(viewDescr.viewModel)() as BaseVMEntityJS
        editor.view.writeData(viewModel)
        val request = SaveEditorDataRequestJS()
        request.objectId = objectId
        request.viewModel = viewModel
        StandardRestClient.standard_standard_saveEditorData(request).then {
            val validation = it.viewValidation
            if(validation != null && ValidationUtilsJS.hasValidationErrors(validation)){
                editor.view.showValidation(validation)
                UiFactory.get().showNotification("Есть ошибки валидации")
                return@then
            }
            editor.view.readData(it.viewModel!!)
            editor.view.configure(it.viewSettings!!)
            editor.setTitle(it.title!!)
            UiFactory.get().showNotification("Объект сохранен")
        }
    }



    override fun isApplicableToObject(objectId: String): Boolean {
        return true
    }

    override fun isVisible(editor: Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>): Boolean {
        return true
    }

    override fun isEnabled(editor: Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>): Boolean {
        return true
    }


}
