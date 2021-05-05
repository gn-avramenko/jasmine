/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.list

import com.gridnine.jasmine.common.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabData
import com.gridnine.jasmine.web.standard.mainframe.MainFrameTabHandler

class WebListMainFrameTabHandler : MainFrameTabHandler<ListWorkspaceItemDTJS>{
    override fun getTabId(obj: ListWorkspaceItemDTJS): String {
        return obj.uid!!
    }

    override suspend fun createTabData(obj: ListWorkspaceItemDTJS, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(obj.displayName!!, WebUiLibraryAdapter.get().createLabel {  }.apply { setText("test") })
    }

    override fun getId(): String {
        return ListWorkspaceItemDTJS::class.simpleName!!
    }

}