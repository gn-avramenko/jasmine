/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.editor


import com.gridnine.jasmine.common.reports.model.domain.BaseReportRequestedParameterJS
import com.gridnine.jasmine.common.reports.model.domain.LocalDateReportRequestedParameterJS
import com.gridnine.jasmine.common.reports.model.domain.ObjectReferenceReportRequestedParameterJS
import com.gridnine.jasmine.common.reports.model.domain.ObjectReferencesReportRequestedParameterJS
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.standard.widgets.AutocompleteHandler
import com.gridnine.jasmine.web.standard.widgets.DateBoxWidget
import com.gridnine.jasmine.web.standard.widgets.EntityMultiValuesWidget
import com.gridnine.jasmine.web.standard.widgets.EntitySelectWidget


interface RequestedParameterWebHandler<T:BaseReportRequestedParameterJS,E:WebNode>{
    fun createEditor():E
    fun getValue(editor:E):T?
    fun setValue(editor: E, value:T?)
    fun showValidation(editor:E, value:String?)
}

class LocalDateRequestedParameterWebHandler:
    RequestedParameterWebHandler<LocalDateReportRequestedParameterJS, DateBoxWidget> {
    override fun createEditor(): DateBoxWidget {
        return DateBoxWidget {
            width = "100%"
        }
    }

    override fun getValue(editor: DateBoxWidget): LocalDateReportRequestedParameterJS? {
        return editor.getValue()?.let {value ->
            LocalDateReportRequestedParameterJS().also {
                it.value = value
            }
        }
    }

    override fun showValidation(editor: DateBoxWidget, value: String?) {
        editor.showValidation(value)
    }

    override fun setValue(editor: DateBoxWidget, value: LocalDateReportRequestedParameterJS?) {
        editor.setValue(value?.value)
    }

}

class ObjectReferenceRequestedParameterWebHandler(private val clsName:String):
    RequestedParameterWebHandler<ObjectReferenceReportRequestedParameterJS, EntitySelectWidget> {
    override fun createEditor(): EntitySelectWidget {
        return EntitySelectWidget{
            width = "100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(clsName)
            showClearIcon = true
            showLinkButton = false
        }
    }

    override fun getValue(editor: EntitySelectWidget): ObjectReferenceReportRequestedParameterJS? {
        return editor.getValue()?.let { value ->
            ObjectReferenceReportRequestedParameterJS().also{
                it.value = value
            }
        }
    }

    override fun showValidation(editor: EntitySelectWidget, value: String?) {
        editor.showValidation(value)
    }

    override fun setValue(editor: EntitySelectWidget, value: ObjectReferenceReportRequestedParameterJS?) {
        editor.setValue(value?.value)
    }

}

class ObjectReferencesRequestedParameterWebHandler(private val clsName:String):
    RequestedParameterWebHandler<ObjectReferencesReportRequestedParameterJS, EntityMultiValuesWidget> {
    override fun createEditor(): EntityMultiValuesWidget {
        return EntityMultiValuesWidget{
            width = "100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(clsName)
            showClearIcon = true
        }
    }

    override fun getValue(editor: EntityMultiValuesWidget): ObjectReferencesReportRequestedParameterJS {
        return ObjectReferencesReportRequestedParameterJS().also{
            it.values.addAll(editor.getValues())
        }
    }

    override fun showValidation(editor: EntityMultiValuesWidget, value: String?) {
        editor.showValidation(value)
    }

    override fun setValue(editor: EntityMultiValuesWidget, value: ObjectReferencesReportRequestedParameterJS?) {
        editor.setValues(value?.values?: emptyList())
    }

}