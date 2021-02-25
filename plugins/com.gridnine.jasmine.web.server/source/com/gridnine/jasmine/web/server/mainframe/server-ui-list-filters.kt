/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.core.utils.TextUtils
import com.gridnine.jasmine.server.standard.model.BaseListFilterValue
import com.gridnine.jasmine.server.standard.model.ListFilterStringValues
import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.widgets.ServerUiTextBoxWidget
import com.gridnine.jasmine.web.server.widgets.ServerUiTextBoxWidgetConfiguration

internal interface ServerUiListFilterHandler<V : BaseListFilterValue, W : ServerUiNode> {
    fun createEditor(): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

internal class StringFilterHandler : ServerUiListFilterHandler<ListFilterStringValues, ServerUiTextBoxWidget> {
    override fun createEditor(): ServerUiTextBoxWidget {
        return ServerUiTextBoxWidget(ServerUiTextBoxWidgetConfiguration{
            width = "100%"
        })
    }

    override fun getValue(editor: ServerUiTextBoxWidget): ListFilterStringValues? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterStringValues()
            result.values.addAll(it.split(",").filter { TextUtils.isNotBlank(it) }.map { it.trim() })
            result
        }
    }

    override fun reset(editor: ServerUiTextBoxWidget) {
        editor.setValue(null)
    }

    override fun isNotEmpty(comp: ServerUiTextBoxWidget): Boolean {
        return getValue(comp)?.values?.isNotEmpty() ?: false
    }
}