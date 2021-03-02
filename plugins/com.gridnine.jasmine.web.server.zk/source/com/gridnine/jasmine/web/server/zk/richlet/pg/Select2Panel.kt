/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.zk.components.*
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTextBox
import org.zkoss.zk.ui.util.Clients

class Select2Panel : ZkServerUiGridLayoutContainer(createConfiguration()){
    init {
        val select2 = run{
            val config = ServerUiSelectConfiguration()
            config.width = "200px"
            config.editable = true
            config.mode = ServerUiSelectDataType.LOCAL
            config.multiple = true
            config.showClearIcon = false
            val result = ZkServerUiSelect(config)
            result.setPossibleValues(arrayListOf(SelectItem("1", "Вариант1"),SelectItem("2", "Вариант2") ))
            result.setValues(arrayListOf(SelectItem("1", "Вариант1"),SelectItem("2", "Вариант2") ))
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val select3 = run{
            val config = ServerUiSelectConfiguration()
            config.width = "200px"
            config.editable = false
            config.mode = ServerUiSelectDataType.LOCAL
            config.multiple = false
            config.showClearIcon = true
            val result = ZkServerUiSelect(config)
            result.setPossibleValues(arrayListOf(SelectItem("1", "Вариант1"),SelectItem("2", "Вариант2") ))
            result.setValues(arrayListOf(SelectItem("1", "Вариант1") ))
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }


        run{
            val config = ServerUiBooleanBoxConfiguration()
            val booleanBox = ZkServerUiBooleanBox(config)
            addRow()
            addCell(ServerUiGridLayoutCell(booleanBox, 1))
        }

        run{
            val buttonConfig = ServerUiLinkButtonConfiguration()
            buttonConfig.width = "200px"
            buttonConfig.title = "context menu"
            val button = ZkServerUiLinkButton(buttonConfig)
            button.setHandler {
                val menuItem = ServerUiContextMenuStandardItem("Меню", null, false){
                    Clients.alert("Меню")
                }
                zkShowContextMenu(arrayListOf(menuItem), 100, 100)
            }
            addRow()
            addCell(ServerUiGridLayoutCell(button, 1))
        }
        val dateBox = run{
            val dateBoxConfig = ServerUiDateBoxConfiguration()
            dateBoxConfig.width = "200px"
            val result = ZkServerUiDateBox(dateBoxConfig)
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val dateTimeBox = run{
            val dateBoxConfig = ServerUiDateTimeBoxConfiguration()
            dateBoxConfig.width = "200px"
            val result = ZkServerUiDateTimeBox(dateBoxConfig)
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val numberBox = run{
            val numberBoxConfig = ServerUiNumberBoxConfiguration()
            numberBoxConfig.width = "200px"
            val result = ZkServerUiNumberBox(numberBoxConfig)
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val menuButton = run{
            val menuButtonConfig = ServerUiMenuButtonConfiguration()
            menuButtonConfig.title = "menu button"
            menuButtonConfig.width = "200px"
            menuButtonConfig.items.add(ServerUiMenuButtonStandardItem("1","menu 1", null, false){
                Clients.alert("Menu 1")
            })
            menuButtonConfig.items.add(ServerUiMenuButtonSeparator())
            menuButtonConfig.items.add(ServerUiMenuButtonStandardItem("2","menu 2", null, false){
                Clients.alert("Menu 2")
            })
            val result = ZkServerUiMenuButton(menuButtonConfig)
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val label = run{
            val result = ZkServerUiLabel(ServerUiLabelConfiguration())
            result.setText("test label")
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val textBox = run{
            val textBoxConfig = ServerUiTextBoxConfiguration()
            textBoxConfig.width = "200px"
            val result = ZkServerUiTextBox(textBoxConfig)
            result.setValue("test")
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }

        val passwordBox = run{
            val passwordBoxConfig = ServerUiPasswordBoxConfiguration()
            passwordBoxConfig.width = "200px"
            val result = ZkServerUiPasswordBox(passwordBoxConfig)
            addRow()
            addCell(ServerUiGridLayoutCell(result, 1))
            result
        }
        run {
            val buttonConfig = ServerUiLinkButtonConfiguration()
            buttonConfig.width = "200px"
            buttonConfig.title = "validate"
            val button = ZkServerUiLinkButton(buttonConfig)
            button.setHandler {
                select3.showValidation("Поле должно быть заполнено")
                dateBox.showValidation("Поле должно быть заполнено")
                dateTimeBox.showValidation("Поле должно быть заполнено")
            }
            addRow()
            addCell(ServerUiGridLayoutCell(button, 1))
            button
        }
        run {
            val buttonConfig = ServerUiLinkButtonConfiguration()
            buttonConfig.width = "200px"
            buttonConfig.title = "reset validation"
            val button = ZkServerUiLinkButton(buttonConfig)
            button.setHandler {
                select3.showValidation(null)
                dateBox.showValidation(null)
                dateTimeBox.showValidation(null)
            }
            addRow()
            addCell(ServerUiGridLayoutCell(button, 1))
            button
        }
        run {
            val buttonConfig = ServerUiLinkButtonConfiguration()
            buttonConfig.width = "200px"
            buttonConfig.title = "show notification"
            val button = ZkServerUiLinkButton(buttonConfig)
            button.setHandler {
                zkShowNotification("<b>Notification</b> 1<br> Notification 2", ServerUiNotificationType.INFO, 3000)
            }
            addRow()
            addCell(ServerUiGridLayoutCell(button, 1))
            button
        }
        run {
            val buttonConfig = ServerUiLinkButtonConfiguration()
            buttonConfig.width = "200px"
            buttonConfig.title = "show dialog"
            val button = ZkServerUiLinkButton(buttonConfig)
            button.setHandler {
                val textBoxConfig = ServerUiTextBoxConfiguration()
                textBoxConfig.width = "200px"
                val textBox = ZkServerUiTextBox(textBoxConfig)
                val dialogConfig = ServerUiDialogConfiguration<ServerUiTextBox>{
                    title = "Диалог"
                    editor = textBox
                    cancelButton()
                }
                showDialog(dialogConfig)
            }
            addRow()
            addCell(ServerUiGridLayoutCell(button, 1))
            button
        }
    }

    companion object{
        private fun createConfiguration(): ServerUiGridLayoutContainerConfiguration {
            val result = ServerUiGridLayoutContainerConfiguration()
            result.width = "200px"
            return result
        }
    }
}