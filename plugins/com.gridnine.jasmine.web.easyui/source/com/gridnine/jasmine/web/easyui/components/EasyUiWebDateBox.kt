/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class EasyUiWebDateBox(configure: WebDateBoxConfiguration.() -> Unit) : WebDateBox,EasyUiComponent {

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq: dynamic = null

    private var enabled = true

    private var storedValue: Date? = null

    private var validationMessage:String? = null

    private val config = WebDateBoxConfiguration()
    init {
        config.configure()
    }

    private val dateFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth() + 1)}-${MiscUtilsJS.fillWithZeros(it.getDate())}" }
    }

    private val dateParser = lambda@{ value: String? ->
        if (MiscUtilsJS.isBlank(value)) {
            return@lambda null
        }
        val components = value!!.split("-")
        try {
            Date(components[0].toInt(), components[1].toInt() - 1, components[2].toInt())
        } catch (e: Throwable) {
            return@lambda null
        }
    }

    override fun getHtml(): String {
        return "<input id=\"dateBox${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getValue(): Date? {
        if (!initialized) {
            return storedValue
        }
        val value = jq.datebox("getText") as String?
        return dateParser(value)
    }

    override fun setValue(value: Date?) {
        if (!initialized) {
            this.storedValue = value
            return
        }
        jq.datebox("setValue", dateFormatter(value))
    }


    override fun decorate() {
        jq = jQuery("#dateBox$uid")
        val icons = arrayListOf<Any>()
        if (config.showClearIcon) {
            icons.add(object {
                val iconCls = "icon-clear"
                val handler = { _: dynamic ->
                    jq.datebox("setValue", null)
                    jq.datebox("getIcon", 0).css("visibility", "hidden")
                }
            })
        }
        jq.datebox(object {
            val closeText = "Закрыть"
            val currentText = "Сегодня"
            val value = dateFormatter.invoke(storedValue)
            val formatter = dateFormatter
            val parser = { value: String? ->
                dateParser(value) ?: Date()
            }
            val icons = icons.toTypedArray()
            val onChange = { newValue: String?, _: String? ->
                jq.datebox("getIcon", 0).css("visibility", if (MiscUtilsJS.isBlank(newValue)) "hidden" else "visible")
                storedValue = dateParser.invoke(newValue)
            }
            val disabled = !enabled
        })
        val tb = jq.datebox("textbox")
        val c = jq.datebox("calendar")
        c.calendar(object {
            val firstDay = 1
            val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
            val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
        })
        tb.on("input") {
            if (config.showClearIcon) {
                val text = jq.datebox("getText") as String?
                jq.datebox("getIcon", 0).css("visibility", if (MiscUtilsJS.isBlank(text)) "hidden" else "visible")
            }
        }
        if (config.showClearIcon && (storedValue == null  || !enabled)) {
            jq.datebox("getIcon", 0).css("visibility", "hidden")
        }
        initialized = true
    }


    override fun setEnabled(value: Boolean) {
        if (enabled != value) {
            enabled = value
            if (initialized) {
                jq.datebox(if (enabled) "enable" else "disable")
                if(config.showClearIcon ){
                    jq.datebox("getIcon",0).css("visibility", if(storedValue == null  || !enabled) "hidden" else "visible")
                }
            }
        }
    }

    override fun showValidation(value: String?) {
        validationMessage = value
        if(initialized){
            showValidationInternal()
        }
    }

    private fun showValidationInternal() {
        if(validationMessage != null){
            val tb =jq.datebox("textbox")
            val spanElm = tb.parent()
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", validationMessage)
            return
        }
        val tb =jq.datetimebox("textbox")
        val spanElm = tb.parent()
        spanElm.css("border-color", "")
        spanElm.removeAttr("title")
    }
    override fun destroy() {
        if(initialized){
            jq.datebox("destroy")
        }
    }

}