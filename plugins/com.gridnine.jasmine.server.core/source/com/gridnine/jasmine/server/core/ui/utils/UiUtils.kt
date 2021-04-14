/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.utils

import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.meta.VVPropertyType
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.components.*

object UiUtils {
    fun confirm(question:String, dialogTitle:String = CoreServerL10nMessagesFactory.Question().toString(), action:()->Unit){
        val label = UiLibraryAdapter.get().createLabel{
            multiline = true
        }
        label.setText(question)
        UiLibraryAdapter.get().showDialog(label){
            title = dialogTitle
            button {
                displayName = CoreServerL10nMessagesFactory.Yes().toString()
                handler = {
                    it.close()
                    action.invoke()
                }
            }
            cancelButton()
        }
    }

//    fun<E:Enum<E>> choseVariant(cls: KClass<E>, aTitle:String = "Выберите вариант", action: (E)->Unit){
//        val layout = UiLibraryAdapter.get().createGridLayoutContainer{
//            columns.add(GridLayoutColumnConfiguration("auto"))
//        }
//        layout.addRow()
//        val widget = ServerUiEnumValueWidget<E>{
//            width = "200px"
//            allowNull = false
//            enumClass = cls
//        }
//        layout.addCell(GridLayoutCell(widget))
//        UiLibraryAdapter.get().showDialog<GridLayoutContainer>{
//            title = aTitle
//            editor = layout
//            button {
//                displayName = "Да"
//                handler = {
//                    val selectedValue = widget.getValue()!!
//                    it.close()
//                    action.invoke(selectedValue)
//                }
//            }
//            cancelButton()
//        }
//    }

    fun hasValidationErrors(vv: BaseVV): Boolean {
        val description = UiMetaRegistry.get().viewValidations[vv::class.qualifiedName]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id) ?: continue
            if (property.type == VVPropertyType.ENTITY && !hasValidationErrors(value as BaseVV)) {
                continue
            }
            return true
        }
        return false
    }
}