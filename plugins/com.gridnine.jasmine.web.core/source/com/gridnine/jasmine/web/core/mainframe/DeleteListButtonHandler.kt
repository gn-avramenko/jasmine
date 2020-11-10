/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.server.core.model.domain.BaseAssetJS
import com.gridnine.jasmine.server.core.model.domain.BaseIndexJS
import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectsRequestJS
import com.gridnine.jasmine.server.standard.rest.DeletedObjectReferenceJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.ListButtonHandler
import com.gridnine.jasmine.web.core.ui.ObjectsList
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import kotlin.browser.window

class DeleteListButtonHandler : ListButtonHandler<BaseIntrospectableObjectJS>{
    override fun getId(): String {
        return "DeleteListButtonHandler"
    }

    override fun getWeight(): Double {
        return 10.0
    }

    override fun isApplicable(objectId: String): Boolean {
        return true
    }

    override fun isEnabled(value: ObjectsList<BaseIntrospectableObjectJS>): Boolean {
        return value.getDataGrid().getSelected().isNotEmpty()
    }

    override fun onClick(value: ObjectsList<BaseIntrospectableObjectJS>) {
        val selected = value.getDataGrid().getSelected()
        if(selected.isNotEmpty()){
            UiUtils.confirm(CoreWebMessagesJS.areYouSureToDelete){
                val refs = selected.map { io ->
                    if(io is BaseIndexJS){
                        DeletedObjectReferenceJS().let {
                            it.objectType = MiscUtilsJS.toServerClassName(io.document.type)
                            it.objectUid = io.document.uid
                            it
                        }
                    } else{
                        (io as BaseAssetJS).let {ba ->
                            DeletedObjectReferenceJS().let {
                                it.objectType = MiscUtilsJS.toServerClassName(ReflectionFactoryJS.get().getQualifiedClassName(ba::class))
                                it.objectUid = ba.uid
                                it
                            }
                        }
                    }
                }
                StandardRestClient.standard_standard_delete(DeleteObjectsRequestJS().let {
                    it.objects.addAll(refs)
                    it
                }).then {
                    UiUtils.showMessage(CoreWebMessagesJS.objectsDeleted)
                    refs.forEach {
                        MainFrame.get().publishEvent(ObjectDeleteEvent(it.objectType+"JS", it.objectUid))
                    }
                }
            }
        }
    }

    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return CoreWebMessagesJS.delete
    }

}