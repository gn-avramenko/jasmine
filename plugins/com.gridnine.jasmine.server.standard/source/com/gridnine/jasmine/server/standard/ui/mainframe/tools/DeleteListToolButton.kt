/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.tools

import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseIndex
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.ObjectDeleteEvent
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import com.gridnine.jasmine.server.standard.helpers.UiListHelper
import com.gridnine.jasmine.server.standard.ui.mainframe.ListToolButton
import com.gridnine.jasmine.server.standard.ui.mainframe.ListWrapper
import com.gridnine.jasmine.server.standard.ui.mainframe.MainFrame

class DeleteListToolButton:ListToolButton<BaseIntrospectableObject>{
    override fun isApplicable(listId: String): Boolean {
        return true
    }

    override fun onClick(value: ListWrapper<BaseIntrospectableObject>) {
        val items = value.getSelectedItems()
        if(items.isEmpty()){
            UiUtils.showError(StandardL10nMessagesFactory.selectItem())
            return
        }
        UiUtils.confirm(StandardL10nMessagesFactory.areYouSureToDelete(value.getSelectedItems().joinToString(",") { UiUtils.toString(it) })){
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
            UiUtils.showInfo(StandardL10nMessagesFactory.objectsDeleted())
            refs.forEach {
                MainFrame.get().publishEvent(ObjectDeleteEvent(it.first, it.second))
            }
        }
    }

    override fun getDisplayName(): String {
        return StandardL10nMessagesFactory.delete()
    }

    override fun getId(): String {
        return DeleteListToolButton::class.java.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}