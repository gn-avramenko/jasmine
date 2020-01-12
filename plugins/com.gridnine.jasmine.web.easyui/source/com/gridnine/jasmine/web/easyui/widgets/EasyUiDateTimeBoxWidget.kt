/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.DateTimeBoxDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.DateTimeBoxWidget
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery
import kotlin.js.Date

class EasyUiDateTimeBoxWidget(uid: String, description: DateTimeBoxDescriptionJS) : DateTimeBoxWidget() {
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized: Boolean = false

    private val dateTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth()+1)}-${TextUtilsJS.fillWithZeros(it.getDate())} ${TextUtilsJS.fillWithZeros(it.getHours())}:${TextUtilsJS.fillWithZeros(it.getMinutes())}" }
    }

    private val dateTimeParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda  null
        }
        val parts = value.split(" ")
        val comps1 = parts[0].split("-")
        val comps2 = parts[1].split(":")
        Date(year = comps1[0].toInt(), month = comps1[1].toInt() - 1, day = comps1[2].toInt(), hour = comps2[0].toInt(), minute = comps2[1].toInt())
    }

    init {

        setData = {
            div.datetimebox("setValue", dateTimeFormatter(it))
        }
        configure = { _: Unit ->
            if (!initialized) {
                div.datetimebox(object {
                    val closeText = "Закрыть"
                    val currentText = "Сегодня"
                    val formatter = dateTimeFormatter
                    val parser = {value:String? ->
                        dateTimeParser(value)?:Date()
                    }
                    val showSeconds =false
                })
                val tb = div.datetimebox("textbox").asDynamic()
                tb.on("input") {
                    val spanElm = tb.parent()
                    spanElm.css("border-color", "")
                    spanElm.removeAttr("title")
                }
                val c = div.datetimebox("calendar").asDynamic()
                c.calendar(object {
                    val firstDay = 1
                    val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
                    val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
                })
                initialized = true
            }
        }
        showValidation = {
            val tb = div.datetimebox("textbox").asDynamic()
            val spanElm = tb.parent()
            if (it != null) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            } else if (!tb.hasClass("text-field-error") as Boolean) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            }
            Unit
        }
        getData = {
            val value = div.datetimebox("getText") as String?
            dateTimeParser(value)
        }
    }

}