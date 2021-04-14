/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.*


class GridCellWidget(caption:String?, val comp: UiNode): BaseNodeWrapper<GridLayoutContainer>(){

    init{
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        if(caption != null){
            _node.addRow()
            val label = UiLibraryAdapter.get().createLabel { }
            label.setText(caption)
            _node.addCell(GridLayoutCell(label, sClass = "jasmine-grid-container-only-bottom-padding"))
        }
        _node.addRow()
        _node.addCell(GridLayoutCell(comp, sClass = "jasmine-grid-container-no-padding"))
    }
}