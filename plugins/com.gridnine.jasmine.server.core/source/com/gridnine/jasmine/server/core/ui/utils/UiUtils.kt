/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.utils

import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.meta.VVPropertyType
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.NotificationType
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.widgets.EnumBoxValueWidget
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

object UiUtils {
    fun confirm(question:String, dialogTitle:String = CoreServerL10nMessagesFactory.Question(), action:()->Unit){
        val label = UiLibraryAdapter.get().createLabel{
            multiline = true
        }
        label.setText(question)
        UiLibraryAdapter.get().showDialog(label){
            title = dialogTitle
            button {
                displayName = CoreServerL10nMessagesFactory.Yes()
                handler = {
                    it.close()
                    action.invoke()
                }
            }
            cancelButton()
        }
    }

    fun<E:Enum<E>> choseVariant(cls: KClass<E>, aTitle:String = CoreServerL10nMessagesFactory.Choose_variant(), action: (E)->Unit){
        val layout = UiLibraryAdapter.get().createGridLayoutContainer{
            columns.add(GridLayoutColumnConfiguration("auto"))
        }
        layout.addRow()
        val widget = EnumBoxValueWidget<E>{
            width = "200px"
            allowNull = false
            enumClass = cls
        }
        layout.addCell(GridLayoutCell(widget))
        UiLibraryAdapter.get().showDialog(widget){
            title = aTitle
            button {
                displayName = CoreServerL10nMessagesFactory.Yes()
                handler = {
                    val selectedValue = widget.getValue()!!
                    it.close()
                    action.invoke(selectedValue)
                }
            }
            cancelButton()
        }
    }

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

    private val dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    fun toString(value:Any?):String{
        return when(value){
            is LocalDateTime -> dateTimeFormatter.format(value)
            else -> value?.toString()?:""
        }
    }

    fun showInfo(text:String){
        UiLibraryAdapter.get().showNotification(text, NotificationType.INFO, 2000)
    }

    fun showError(text:String){
        UiLibraryAdapter.get().showNotification(text, NotificationType.ERROR, 3000)
    }


}