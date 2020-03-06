/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.complexDocument

import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.Dialog
import com.gridnine.jasmine.web.core.ui.DialogButtonHandler
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.core.utils.TextUtilsJS

class SandboxComplexDocumentViewInterceptor:ViewInterceptor<SandboxComplexDocumentEditorVMJS,SandboxComplexDocumentEditorVSJS,SandboxComplexDocumentEditorVVJS,SandboxComplexDocumentEditorView>{
    override fun onCreate(view: SandboxComplexDocumentEditorView) {
        view.generalTile.compactView.stringProperty.valueChangeListener = {newValue:String?, _:String? ->
            view.generalTile.fullView.stringProperty.setData(newValue)
        }
    }

}

class  SandboxComplexDocumentVariantButtonHandler: DialogButtonHandler<SandboxSelectVariantDialogVMJS, SandboxSelectVariantDialogVSJS, SandboxSelectVariantDialogVVJS, SandboxSelectVariantDialogView> {
    override fun handle(dialog: Dialog<SandboxSelectVariantDialogVMJS, SandboxSelectVariantDialogVSJS, SandboxSelectVariantDialogVVJS, SandboxSelectVariantDialogView>) {
        val result = dialog.view.variantSelect.getData()
        dialog.close()
        val navigatorWidget = dialog.properties["widget"] as NavigatorWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>
        when(result!!.id){
            "1"->{
                val model = SandboxComplexDocumentVariant1ViewVMJS()
                model.uid = TextUtilsJS.createUUID()
                model.caption = "Новый вариант"
                navigatorWidget.add(model, SandboxComplexDocumentVariant1ViewVSJS(), null)
            }
            "2"->{
                val model = SandboxComplexDocumentVariant2ViewVMJS()
                model.uid = TextUtilsJS.createUUID()
                model.caption = "Новый вариант"
                navigatorWidget.add(model, SandboxComplexDocumentVariant2ViewVSJS(), null)
            }
        }
    }


}

class SandboxComplexDocumentVariantButtonsHandler : NavigatorButtonsHandler<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>{
    override fun onAdd(widget: NavigatorWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>, selected: BaseVMEntityJS) {

        val settings = SandboxSelectVariantDialogVSJS()
        val selectSettings = SelectConfigurationJS()
        settings.variantSelect = selectSettings
        selectSettings.nullAllowed = false
        selectSettings.possibleValues.add(SelectItemJS("1", "Вариант 1"))
        selectSettings.possibleValues.add(SelectItemJS("2", "Вариант 2"))
        val model = SandboxSelectVariantDialogVMJS()
        model.variantSelect = selectSettings.possibleValues[0]

        val dialog = UiFactory.get().showDialog(SandboxSelectVariantDialog(),model , settings)
        dialog.properties["widget"] = widget

    }

    override fun onDelete(widget: NavigatorWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>, selected: BaseVMEntityJS) {
        widget.remove(selected.uid!!)
    }

}