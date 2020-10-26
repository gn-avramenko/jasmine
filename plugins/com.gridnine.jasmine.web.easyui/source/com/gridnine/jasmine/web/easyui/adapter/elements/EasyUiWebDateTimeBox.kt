/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Date

class EasyUiWebDateTimeBox(private val parent:WebComponent?, configure: WebDateTimeBoxConfiguration.()->Unit) :WebDateTimeBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var prompt:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false
    private var showSeconds = false

    private var value:Date? = null
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebDateTimeBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        showClearIcon = configuration.showClearIcon
        showSeconds = configuration.showSeconds
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

    override fun getHtml(): String {
        return "<input id=\"dateTimeBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun getValue(): Date? {
        if(!initialized){
            return value
        }
        val value = jq.datetimebox("getText") as String?
        return dateTimeParser(value)
    }

    override fun setValue(value: Date?){
        if(!initialized){
            this.value = value
            return
        }
        jq.datetimebox("setValue", dateTimeFormatter(value))
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#dateTimeBox$uid")
        var icons = arrayListOf<Any>()
        if(showClearIcon){
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
            val showSeconds = showClearIcon
            val parser = { value:String? ->
                dateTimeParser(value)?:Date()
            }
            val icons = icons.toTypedArray()
            val onChange = {newValue:String?,_:String? ->
                jq.datetimebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(newValue)) "hidden" else "visible")
            }
        })
        val tb = jq.datetimebox("textbox")
        val c = jq.datetimebox("calendar")
        c.calendar(object {
            val firstDay = 1
            val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
            val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
        })
        tb.on("input") {
            if(showClearIcon){
                val text = jq.datetimebox("getText") as String?
                jq.datetimebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(text)) "hidden" else "visible")
            }
        }
        if(showClearIcon && value == null){
            jq.datetimebox("getIcon",0).css("visibility","hidden")
        }
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}