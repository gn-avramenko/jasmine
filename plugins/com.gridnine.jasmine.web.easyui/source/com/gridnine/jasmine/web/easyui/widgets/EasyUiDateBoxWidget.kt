/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.DateBoxWidget
import com.gridnine.jasmine.web.core.model.ui.DateboxDescriptionJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery
import kotlin.js.Date

class EasyUiDateBoxWidget(uid: String, description: DateboxDescriptionJS) : DateBoxWidget() {
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized: Boolean = false

    private val dateFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth() + 1)}-${TextUtilsJS.fillWithZeros(it.getDate())}" }
    }

    private val dateParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda null
        }
        val components = value.split("-")
        Date(components[0].toInt(), components[1].toInt() - 1, components[2].toInt())
    }

    init {

        setData = {
            div.datebox("setValue", dateFormatter(it))
        }
        configure = { _: Unit ->
            if (!initialized) {
                div.datebox(object {
                    val closeText = "Закрыть"
                    val currentText = "Сегодня"
                    val formatter = dateFormatter
                    val parser = {value:String ->
                        dateParser(value)?:Date()
                    }
                })
                val tb = div.datebox("textbox").asDynamic()
                tb.on("input") {
                    val spanElm = tb.parent()
                    spanElm.css("border-color", "")
                    spanElm.removeAttr("title")
                }
                val c = div.datebox("calendar").asDynamic()
                c.calendar(object {
                    val firstDay = 1
                    val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
                    val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
                })
                initialized = true
            }
        }
        showValidation = {
            val tb = div.datebox("textbox").asDynamic()
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
            val value = div.datebox("getText") as String?
            dateParser(value)
        }
    }

}