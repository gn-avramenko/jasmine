/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.list

import com.gridnine.jasmine.common.core.model.BaseAssetJS
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.core.model.BaseIndexJS
import com.gridnine.jasmine.common.standard.rest.DeletedObjectReferenceJS
import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectsRequestJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.ObjectDeleteEvent
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils

class DeleteSelectedItemsListButtonHandler:ListLinkButtonHandler<BaseIdentityJS>{
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        if(selected.isNotEmpty()){
            StandardUiUtils.confirm("<nobr>Вы действительно хотите удалить объекты?</nobr>"){
                val refs = selected.map { io ->
                    if(io is BaseIndexJS){
                        DeletedObjectReferenceJS().let {
                            it.objectType = MiscUtilsJS.toServerClassName(io.document!!.type)
                            it.objectUid = io.document!!.uid
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
                launch {
                    StandardRestClient.standard_standard_delete(DeleteObjectsRequestJS().apply {
                        objects.addAll(refs)
                    })
                    StandardUiUtils.showMessage("Объекты успешно удалены")
                    refs.forEach {
                        MainFrame.get().publishEvent(ObjectDeleteEvent(it.objectType + "JS", it.objectUid))
                    }
                }
            }
        }
    }

}