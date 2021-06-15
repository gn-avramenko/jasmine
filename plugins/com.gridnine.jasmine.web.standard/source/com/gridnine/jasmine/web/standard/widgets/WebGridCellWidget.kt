/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode


class WebGridCellWidget(caption:String?, comp: WebNode): BaseWebNodeWrapper<WebGridLayoutWidget>(){

    init{
        _node = WebGridLayoutWidget {
            width = "100%"
        }.also {
            it.setColumnsWidths("100%")
            if(caption != null){
                val label = WebLabelWidget(caption)
                it.addRow(null, arrayListOf(WebGridLayoutWidgetCell(label, sClass = "jasmine-grid-container-only-bottom-padding")))
            }
            it.addRow(null, arrayListOf(WebGridLayoutWidgetCell(comp, sClass ="jasmine-grid-container-no-padding")))
        }
    }
}