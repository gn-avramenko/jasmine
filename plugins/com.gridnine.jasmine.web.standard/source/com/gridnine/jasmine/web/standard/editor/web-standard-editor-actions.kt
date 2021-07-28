/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.editor

import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.common.standard.model.rest.GetVersionsMetadataRequestJS
import com.gridnine.jasmine.common.standard.model.rest.SaveEditorDataRequestJS
import com.gridnine.jasmine.common.standard.rest.ObjectVersionMetaDataJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import kotlin.js.Date

class ObjectEditorEditStateActionDisplayHandler : ObjectEditorActionDisplayHandler<WebEditor<*, *, *>> {
    override fun isEnabled(editor: ObjectEditor<WebEditor<*, *, *>>): Boolean {
        return !editor.isReadonly()
    }

    override fun isVisible(editor: ObjectEditor<WebEditor<*, *, *>>): Boolean {
        return true
    }
}

class SaveObjectEditorObjectButtonHandler : ObjectEditorTool<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>> {
    override suspend fun invoke(editor: ObjectEditor<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>>) {
        val vm = editor.getEditor().getData()
        val request = SaveEditorDataRequestJS()
        request.objectId = editor.objectType
        request.objectUid = editor.objectUid
        request.viewModel = vm
        val response = StandardRestClient.standard_standard_saveEditorData(request)
        val validation = response.viewValidation

        if (StandardUiUtils.hasValidationErrors(validation)) {
            editor.getEditor().showValidation(validation)
            return
        }
        editor.getEditor().readData(response.viewModel!!, response.viewSettings)
        editor.updateTitle(response.title)
        response.newUid?.let { editor.objectUid = it }
        StandardUiUtils.showMessage(WebMessages.Object_saved)
    }
}

