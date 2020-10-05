/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.standard.model.rest.BaseListFilterValueDTJS
import com.gridnine.jasmine.server.standard.model.rest.ListFilterBooleanValuesDTJS
import com.gridnine.jasmine.server.standard.model.rest.ListFilterStringValuesDTJS
import com.gridnine.jasmine.server.standard.model.ui.YesNoEnumJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebTextBox
import com.gridnine.jasmine.web.core.ui.widgets.EnumComboboxWidget
import com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget

internal interface ListFilterHandler<V : BaseListFilterValueDTJS, W : WebComponent> {
    fun createEditor(parent: WebComponent): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

internal class StringFilterHandler : ListFilterHandler<ListFilterStringValuesDTJS, TextBoxWidget> {
    override fun createEditor(parent: WebComponent): TextBoxWidget {
        return TextBoxWidget(parent, {
            width = "100%"
        })
    }

    override fun getValue(editor: TextBoxWidget): ListFilterStringValuesDTJS? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterStringValuesDTJS()
            result.values.addAll(it.split(","))
            result
        }
    }

    override fun reset(editor: TextBoxWidget) {
        editor.setValue(null)
    }

    override fun isNotEmpty(comp: TextBoxWidget): Boolean {
        return getValue(comp)?.values?.isNotEmpty() ?: false
    }
}

internal class BooleanFilterHandler : ListFilterHandler<ListFilterBooleanValuesDTJS, EnumComboboxWidget<YesNoEnumJS>> {
    override fun createEditor(parent: WebComponent): EnumComboboxWidget<YesNoEnumJS> {
        val widget = EnumComboboxWidget<YesNoEnumJS>(parent, {
            width = "100%"
            enumClass = YesNoEnumJS::class
        })
        widget.setValue(YesNoEnumJS.NOT_IMPORTANT)
        return widget
    }

    override fun getValue(editor: EnumComboboxWidget<YesNoEnumJS>): ListFilterBooleanValuesDTJS? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterBooleanValuesDTJS()
            result.value = when(it){
                YesNoEnumJS.YES -> true
                YesNoEnumJS.NO -> false
                YesNoEnumJS.NOT_IMPORTANT -> null
            }
            result
        }
    }

    override fun reset(editor: EnumComboboxWidget<YesNoEnumJS>) {
        editor.setValue(YesNoEnumJS.NOT_IMPORTANT)
    }

    override fun isNotEmpty(comp: EnumComboboxWidget<YesNoEnumJS>): Boolean {
        return comp.getValue() != YesNoEnumJS.NOT_IMPORTANT
    }


}
