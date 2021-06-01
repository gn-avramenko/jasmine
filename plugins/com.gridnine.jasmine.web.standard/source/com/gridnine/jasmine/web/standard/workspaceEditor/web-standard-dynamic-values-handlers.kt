/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.standard.model.domain.DynamicCriterionDateValueTypeJS
import com.gridnine.jasmine.common.standard.model.rest.DynamicCriterionDateValueDTJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.widgets.EnumValueWidget
import com.gridnine.jasmine.web.standard.widgets.IntegerNumberBoxWidget

class DateDynamicValueEditor : BaseWebNodeWrapper<WebGridLayoutContainer>(){
    private val correctionValue:IntegerNumberBoxWidget

    private val correctionType: EnumValueWidget<DynamicCriterionDateValueTypeJS>

    private var uidValue:String? = null
    init {
        correctionValue = IntegerNumberBoxWidget {
            width = "100%"
            nullable = false
        }
        correctionType = EnumValueWidget {
            width = "100%"
            allowNull = false
            enumClass = DynamicCriterionDateValueTypeJS::class
        }
        _node = WebUiLibraryAdapter.get().createGridContainer {
            width = "100%"
            noPadding = true
            column("auto")
            column("50px")
            column("100%")
            row {
                cell(WebUiLibraryAdapter.get().createLabel {
                    width = "100%"
                    height = "100%"
                }.apply {
                    setText("<nobr>&nbspс коррекцией:&nbsp</nobr>")
                })
                cell(correctionValue)
                cell(correctionType)
            }
        }
    }

    fun getValue():DynamicCriterionDateValueDTJS {
       return DynamicCriterionDateValueDTJS().apply {
           uid = uidValue?:MiscUtilsJS.createUUID()
           correction = correctionValue.getValue()?:0
           valueType = correctionType.getValue()?:DynamicCriterionDateValueTypeJS.DAYS
       }
    }

    fun setValue(value:DynamicCriterionDateValueDTJS){
        uidValue = value.uid
        correctionType.setValue(value.valueType)
        correctionValue.setValue(value.correction)
    }
}

class DateDynamicValueEditorHandler: WebDynamicCriterionValueEditorHandler<DynamicCriterionDateValueDTJS, DateDynamicValueEditor>{
    override fun getId(): String {
        return ReflectionFactoryJS.get().getQualifiedClassName(DynamicCriterionDateValueDTJS::class)
    }

    override fun createEditor(): DateDynamicValueEditor {
        return DateDynamicValueEditor()
    }

    override fun setValue(editor: DateDynamicValueEditor, value: DynamicCriterionDateValueDTJS) {
        editor.setValue(value)
    }

    override fun getValue(editor: DateDynamicValueEditor): DynamicCriterionDateValueDTJS {
        return editor.getValue()
    }


}