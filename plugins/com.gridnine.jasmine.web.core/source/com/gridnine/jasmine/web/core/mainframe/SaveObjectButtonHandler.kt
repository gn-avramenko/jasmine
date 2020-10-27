/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataRequestJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.ObjectEditorButton
import com.gridnine.jasmine.web.core.ui.WebEditor
import com.gridnine.jasmine.web.core.utils.ValidationUtilsJS

class SaveObjectButtonHandler:ObjectEditorButton<WebEditor<BaseVMJS, BaseVSJS,BaseVVJS>> {
    override fun getId(): String {
        return SaveObjectButtonHandler::class.simpleName!!
    }

    override fun isApplicable(objectId: String): Boolean {
        return true
    }

    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return CoreWebMessagesJS.save
    }

    override fun getWeight(): Double {
        return 100.toDouble()
    }



    override fun onClick(value: ObjectEditor<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>>) {
        val vm = value.rootWebEditor.getData()
        val request = SaveEditorDataRequestJS()
        request.objectId = value.obj.objectType
        request.objectUid =value.obj.objectUid
        request.viewModel = vm
        StandardRestClient.standard_standard_saveEditorData(request).then {
            val validation = it.viewValidation
            if(validation != null && ValidationUtilsJS.hasValidationErrors(validation)){
                value.rootWebEditor.showValidation(validation)
                return@then
            }
            value.rootWebEditor.readData(it.viewModel!!,it.viewSettings!!)
            value.updateTitle(it.title)
            if(it.newUid != null){
                value.obj.objectUid = it.newUid
            }
        }
    }

    override fun isEnabled(value: ObjectEditor<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>>): Boolean {
        return !value.readOnly
    }

}