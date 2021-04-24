/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.ui

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.reports.model.misc.BaseReportRequestedParameter
import com.gridnine.jasmine.common.reports.model.misc.LocalDateReportRequestedParameter
import com.gridnine.jasmine.common.reports.model.misc.ObjectReferenceReportRequestedParameter
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.widgets.AutocompleteHandler
import com.gridnine.jasmine.server.core.ui.widgets.DateBoxWidget
import com.gridnine.jasmine.server.core.ui.widgets.EntityValueWidget

interface RequestedParameterUiHandler<T:BaseReportRequestedParameter,E:UiNode>{
    fun createEditor():E
    fun getValue(editor:E):T?
    fun setValue(editor: E, value:T?)
    fun showValidation(editor:E, value:String?)
}

class LocalDateRequestedParameterUiHandler: RequestedParameterUiHandler<LocalDateReportRequestedParameter,DateBoxWidget>{
    override fun createEditor(): DateBoxWidget {
        return DateBoxWidget {
            width = "100%"
        }
    }

    override fun getValue(editor: DateBoxWidget): LocalDateReportRequestedParameter? {
        return editor.getValue()?.let {value ->
            LocalDateReportRequestedParameter().let {
                it.value = value
                it
            }
        }
    }

    override fun showValidation(editor: DateBoxWidget, value: String?) {
        editor.showValidation(value)
    }

    override fun setValue(editor: DateBoxWidget, value: LocalDateReportRequestedParameter?) {
        editor.setValue(value?.value)
    }

}

class ObjectReferenceRequestedParameterUiHandler(private val clsName:String): RequestedParameterUiHandler<ObjectReferenceReportRequestedParameter,EntityValueWidget<BaseIdentity>>{
    override fun createEditor(): EntityValueWidget<BaseIdentity> {
        return EntityValueWidget{
            width = "100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(clsName)
            showClearIcon = true
        }
    }

    override fun getValue(editor: EntityValueWidget<BaseIdentity>): ObjectReferenceReportRequestedParameter? {
        return editor.getValue()?.let { value ->
            ObjectReferenceReportRequestedParameter().let{
                it.value = value
                it
            }
        }
    }

    override fun showValidation(editor: EntityValueWidget<BaseIdentity>, value: String?) {
        editor.showValidation(value)
    }

    override fun setValue(editor: EntityValueWidget<BaseIdentity>, value: ObjectReferenceReportRequestedParameter?) {
        editor.setValue(value?.value)
    }


}