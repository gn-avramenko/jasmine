/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

class EasyUiEditorButtonWidget<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS, V:BaseView<VM,VS,VV>>(id: String, handler: BaseEditorToolButtonHandler<VM,VS,VV,V>, editor:Editor<VM,VS,VV,V>) : ToolButtonWidget() {
    val div: JQuery = jQuery("#$id")

    init {
        div.linkbutton(object {
            val onClick = {
                handler.onClick(editor)
            }
        })
        setEnabled = {value -> if(value) div.linkbutton("enable") else div.linkbutton("disable")}
        setVisible = {}
    }

}