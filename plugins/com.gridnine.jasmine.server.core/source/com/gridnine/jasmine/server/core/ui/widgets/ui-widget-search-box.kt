/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.TextBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter


class SearchBoxWidget(config: SearchBoxWidgetConfiguration): BaseNodeWrapper<TextBox>(){

    private var searchHandler: ((String?) ->Unit)? = null

    init{
        val comp = UiLibraryAdapter.get().createTextBox{
            width = config.width
            height = config.height
        }
        comp.setActionListener {
            searchHandler?.invoke(it)
        }
        _node = comp
    }

    fun setSearchHandler(handler: (String?) ->Unit){
        searchHandler = handler
    }

    fun getValue() = _node.getValue()
}

class SearchBoxWidgetConfiguration:BaseWidgetConfiguration()