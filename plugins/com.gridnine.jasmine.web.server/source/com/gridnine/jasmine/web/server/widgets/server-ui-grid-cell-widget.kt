/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.web.server.components.*

class ServerUiGridCellWidget(caption:String?, val comp: ServerUiNode): BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    init{
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        if(caption != null){
            _node.addRow()
            val label = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration{})
            label.setText(caption)
            _node.addCell(ServerUiGridLayoutCell(label, sClass = "jasmine-grid-container-only-bottom-padding"))
        }
        _node.addRow()
        _node.addCell(ServerUiGridLayoutCell(comp, sClass = "jasmine-grid-container-no-padding"))
    }
}