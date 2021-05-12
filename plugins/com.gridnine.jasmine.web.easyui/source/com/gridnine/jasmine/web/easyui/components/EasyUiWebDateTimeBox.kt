/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class EasyUiWebDateTimeBox(configure: WebDateTimeBoxConfiguration.()->Unit) :WebDateTimeBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null
    private var enabled = true
    private var validationMessage:String? = null

    private var storedValue:Date? = null
    private val config = WebDateTimeBoxConfiguration()
    init {
        config.configure()
    }

    private val dateTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth()+1)}-${MiscUtilsJS.fillWithZeros(it.getDate())} ${MiscUtilsJS.fillWithZeros(it.getHours())}:${MiscUtilsJS.fillWithZeros(it.getMinutes())}" }
    }

    private val dateTimeParser = lambda@{ value: String? ->
        if (MiscUtilsJS.isBlank(value)) {
            return@lambda  null
        }
        try {
            val parts = value!!.split(" ")
            val comps1 = parts[0].split("-")
            val comps2 = parts[1].split(":")
            Date(year = comps1[0].toInt(), month = comps1[1].toInt() - 1, day = comps1[2].toInt(), hour = comps2[0].toInt(), minute = comps2[1].toInt())
        }catch (e:Throwable){
            return@lambda null
        }
    }

    override fun getId(): String {
        return "dateTimeBox${uid}"
    }
    override fun getHtml(): String {
        return "<input id=\"dateTimeBox${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getValue(): Date? {
        if(!initialized){
            return storedValue
        }
        val value = jq.datetimebox("getText") as String?
        return dateTimeParser(value)
    }

    override fun setValue(value: Date?){
        if(!initialized){
            this.storedValue = value
            return
        }
        jq.datetimebox("setValue", dateTimeFormatter(value))
    }



    override fun decorate() {
        jq = jQuery("#dateTimeBox$uid")
        val icons = arrayListOf<Any>()
        if(config.showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.datetimebox("setValue", null)
                    jq.datetimebox("getIcon",0).css("visibility","hidden")
                }
            })
        }
        jq.datetimebox(object{
            val closeText = "Закрыть"
            val currentText = "Сегодня"
            val formatter = dateTimeFormatter
            val showSeconds = config.showSeconds
            val value = dateTimeFormatter.invoke(storedValue)
            val parser = { value:String? ->
                dateTimeParser(value)?:Date()
            }
            val icons = icons.toTypedArray()
            val onChange = {newValue:String?,_:String? ->
                jq.datetimebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(newValue)) "hidden" else "visible")
                storedValue = dateTimeParser.invoke(newValue)
            }
            val disabled = !enabled
        })
        val tb = jq.datetimebox("textbox")
        val c = jq.datetimebox("calendar")
        c.calendar(object {
            val firstDay = 1
            val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
            val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
        })
        tb.on("input") {
            if(config.showClearIcon){
                val text = jq.datetimebox("getText") as String?
                jq.datetimebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(text)) "hidden" else "visible")
            }
        }
        if(config.showClearIcon && (storedValue == null  || !enabled)){
            jq.datetimebox("getIcon",0).css("visibility","hidden")
        }
        showValidationInternal()
        initialized = true
    }

    override fun setEnabled(value: Boolean) {
        if (enabled != value) {
            enabled = value
            if (initialized) {
                jq.datetimebox(if (enabled) "enable" else "disable")
                if(config.showClearIcon ){
                    jq.datetimebox("getIcon",0).css("visibility", if(storedValue == null  || !enabled) "hidden" else "visible")
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
            val tb =jq.datetimebox("textbox")
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
            jq.datetimebox("destroy")
        }
    }

}