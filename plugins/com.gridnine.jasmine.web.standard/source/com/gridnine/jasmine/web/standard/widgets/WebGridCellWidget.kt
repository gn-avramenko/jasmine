/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.components.WebNode


class WebGridCellWidget(caption:String?, comp: WebNode): BaseWebNodeWrapper<WebGridLayoutContainer>(){

    init{
        _node = WebUiLibraryAdapter.get().createGridContainer {
            width = "100%"
            column("100%")
            if(caption != null){
                row {
                    val label = WebUiLibraryAdapter.get().createLabel{}
                    label.setText(caption)
                    cell(label, sClass = "jasmine-grid-container-only-bottom-padding")
                }
            }
            row{
                cell(comp, sClass = "jasmine-grid-container-no-padding")
            }
        }
    }
}