/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.ui.BaseListToolButtonHandler
import com.gridnine.jasmine.web.core.model.ui.EntityList
import com.gridnine.jasmine.web.core.model.ui.ToolButtonWidget
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

class EasyUiListButtonWidget<E:BaseEntityJS>(id: String, handler: BaseListToolButtonHandler<E>, list:EntityList<E>) : ToolButtonWidget() {
    val div: JQuery = jQuery("#$id")

    init {
        div.linkbutton(object {
            val onClick = {
                handler.onClick(list)
            }
        })
        setEnabled = {value -> if(value) div.linkbutton("enable") else div.linkbutton("disable")}
        setVisible = {}
    }

}