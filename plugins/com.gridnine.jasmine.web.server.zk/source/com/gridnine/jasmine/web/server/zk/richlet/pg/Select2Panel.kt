/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.*
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.*
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiTextBox
import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import org.zkoss.zul.Vbox

class Select2Panel :Vbox{
    constructor(){
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
            appendChild(result.getComponent())
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
            appendChild(result.getComponent())
            result
        }


        run{
            val config = ServerUiBooleanBoxConfiguration()
            val booleanBox = ZkServerUiBooleanBox(config)
            appendChild(booleanBox.getComponent())
        }

        run{
            val button = Button()
            button.width = "200px"
            button.label = "context menu"
            button.addEventListener(Events.ON_CLICK) {
                val menuItem = ServerUiContextMenuStandardItem("Меню", null, false){
                    Clients.alert("Меню")
                }
                showMenu(arrayListOf(menuItem), 100, 100)
            }
            appendChild(button)
        }
        val dateBox = run{
            val dateBoxConfig = ServerUiDateBoxConfiguration()
            dateBoxConfig.width = "200px"
            val result = ZkServerUiDateBox(dateBoxConfig)
            appendChild(result.getComponent())
            result
        }

        val dateTimeBox = run{
            val dateBoxConfig = ServerUiDateTimeBoxConfiguration()
            dateBoxConfig.width = "200px"
            val result = ZkServerUiDateTimeBox(dateBoxConfig)
            appendChild(result.getComponent())
            result
        }

        val numberBox = run{
            val numberBoxConfig = ServerUiNumberBoxConfiguration()
            numberBoxConfig.width = "200px"
            val result = ZkServerUiNumberBox(numberBoxConfig)
            appendChild(result.getComponent())
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
            appendChild(result.getComponent())
            result
        }

        val label = run{
            val result = ZkServerUiLabel(SeverUiLabelConfiguration())
            result.setText("test label")
            appendChild(result.getComponent())
            result
        }

        val textBox = run{
            val textBoxConfig = ServerUiTextBoxConfiguration()
            textBoxConfig.width = "200px"
            val result = ZkServerUiTextBox(textBoxConfig)
            result.setValue("test")
            appendChild(result.getComponent())
            result
        }

        val passwordBox = run{
            val passwordBoxConfig = ServerUiPasswordBoxConfiguration()
            passwordBoxConfig.width = "200px"
            val result = ZkServerUiPasswordBox(passwordBoxConfig)
            appendChild(result.getComponent())
            result
        }
        val button = Button()
        button.width = "200px"
        button.label = "delete"
        button.addEventListener(Events.ON_CLICK) {
            select3.showValidation("Поле должно быть заполнено")
            dateBox.showValidation("Поле должно быть заполнено")
            dateTimeBox.showValidation("Поле должно быть заполнено")
        }
        appendChild(button)
        val button2 = Button()
        button2.width = "200px"
        button2.label = "reset validation"
        button2.addEventListener(Events.ON_CLICK) {
            select3.showValidation(null)
            dateBox.showValidation(null)
            dateTimeBox.showValidation(null)

        }
        appendChild(button2)
    }
}