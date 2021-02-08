/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import org.zkoss.zk.ui.event.Events
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
        val button = Button()
        button.width = "200px"
        button.label = "delete"
        button.addEventListener(Events.ON_CLICK) {
            select3.validation ="Поле должно быть заполнено"
        }

        appendChild(button)
    }
}