/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.*
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.*
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
        val select2 = Select2()
        select2.width = "200px"
        select2.configuration.dataSourceType = Select2DataSourceType.LOCAL
        select2.configuration.isMultiple = true
        select2.configuration.possibleValues = arrayListOf(Select2Option("1", "Вариант1"), Select2Option("2", "Вариант2"), Select2Option("3", "Вариант3"))
        select2.selectedValues = arrayListOf(Select2Option("1", "Вариант1"), Select2Option("2", "Вариант2"))
//        select2.addChangeListener{
//            it as Select2ChangeEvent
//            println("selected ${it.selectedValues}")
//        }
        select2.tooltiptext = "Test"
        //select2.selectedValues = arrayListOf(Select2Option("1", "Вариант1"),Select2Option("2", "Вариант2"))
        appendChild(select2)

        val select3 = Select2()
        select3.width = "200px"
        select3.configuration.dataSourceType = Select2DataSourceType.LOCAL
        select3.configuration.isMultiple = false
        select3.configuration.isShowClearIcon = true
        select3.configuration.possibleValues = arrayListOf(Select2Option("1", "Вариант1"), Select2Option("2", "Вариант2"), Select2Option("3", "Вариант3"))
        select3.selectedValues = arrayListOf(Select2Option("1", "Вариант1"))
//        select2.addChangeListener{
//            it as Select2ChangeEvent
//            println("selected ${it.selectedValues}")
//        }
        //select2.selectedValues = arrayListOf(Select2Option("1", "Вариант1"),Select2Option("2", "Вариант2"))
        appendChild(select3)
        val textbox = Textbox()
        textbox.width = "200px"
        textbox.setWidgetListener("onChanging", "console.log(event)")
        appendChild(textbox)


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
            val result = ZkServerUiLabel()
            result.setText("test label")
            appendChild(result.getComponent())
            result
        }

        val button = Button()
        button.width = "200px"
        button.label = "delete"
        button.addEventListener(Events.ON_CLICK) {
            select3.validation ="Поле должно быть заполнено"
            dateBox.showValidation("Поле должно быть заполнено")
            dateTimeBox.showValidation("Поле должно быть заполнено")
        }
        appendChild(button)
        val button2 = Button()
        button2.width = "200px"
        button2.label = "reset validation"
        button2.addEventListener(Events.ON_CLICK) {
            select3.validation = null
            dateBox.showValidation(null)
            dateTimeBox.showValidation(null)

        }
        appendChild(button2)
    }
}