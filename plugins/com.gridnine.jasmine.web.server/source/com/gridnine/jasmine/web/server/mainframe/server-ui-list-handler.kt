/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.widgets.ServerUiSearchBoxWidget
import com.gridnine.jasmine.web.server.widgets.ServerUiSearchBoxWidgetConfiguration

class ServerUiListHandler : ServerUiMainFrameTabHandler<ListWorkspaceItem>{
    override fun getTabId(obj: ListWorkspaceItem): String {
        return obj.uid
    }

    override fun createTabData(obj: ListWorkspaceItem, callback: ServerUiMainFrameTabCallback): ServerUiMainFrameTabData {
        return ServerUiMainFrameTabData(obj.displayName?:"", ServerUiMainframeListComponent(obj))
    }

}

class ServerUiMainframeListComponent(item: ListWorkspaceItem):BaseServerUiNodeWrapper(){
    init{
        val listBorder  = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration{
            width ="100%"
            height = "100%"
        })
        val northContent = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width ="100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        northContent.addRow()
        northContent.addCell(ServerUiGridLayoutCell(null, 1))
        val searchWidget = ServerUiSearchBoxWidget(ServerUiSearchBoxWidgetConfiguration{
            width = "200px"
        })
        northContent.addCell(ServerUiGridLayoutCell(searchWidget, 1))
        val northRegion = ServerUiBorderContainerRegion{
            collapsible = false
            showSplitLine = false
            showBorder  =false
            content = northContent
        }
        listBorder.setNorthRegion(northRegion)
        _node = listBorder
    }
}