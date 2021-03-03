/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.tools

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.standard.helpers.UiListHelper
import com.gridnine.jasmine.web.server.common.ServerUiMainFrameMenuButton
import com.gridnine.jasmine.web.server.common.ServerUiObjectDeleteEvent
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiNotificationType
import com.gridnine.jasmine.web.server.mainframe.ServerUiListToolButton
import com.gridnine.jasmine.web.server.mainframe.ServerUiListWrapper
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrame
import com.gridnine.jasmine.web.server.utils.ServerUiUtils

class ServerUiDeleteListToolButton:ServerUiListToolButton<BaseIntrospectableObject>{
    override fun isApplicable(listId: String): Boolean {
        return true
    }

    override fun onClick(value: ServerUiListWrapper<BaseIntrospectableObject>) {
        val items = value.getSelectedItems()
        if(items.isEmpty()){
            ServerUiLibraryAdapter.get().showNotification("Выберите строки", ServerUiNotificationType.INFO, 2000)
            return
        }
        ServerUiUtils.confirm("Вы действительно хотите удалить объекты?"){
            val refs = items.map { io ->
                if(io is BaseIndex<*>){
                    Pair(io.document!!.type.java.name, io.document!!.uid)
                } else{
                    (io as BaseAsset).let {ba ->
                        Pair(ba::class.java.name, ba.uid)
                    }
                }
            }
            UiListHelper.deleteObjects(refs)
            ServerUiLibraryAdapter.get().showNotification("Объекты удалены", ServerUiNotificationType.INFO, 2000)
            refs.forEach {
                ServerUiMainFrame.get().publishEvent(ServerUiObjectDeleteEvent(it.first, it.second))
            }
        }
    }

    override fun getDisplayName(): String {
        return "Удалить"
    }

    override fun getId(): String {
        return ServerUiDeleteListToolButton::class.java.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}