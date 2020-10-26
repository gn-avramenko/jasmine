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

class EasyUiWebDateBox(private val parent:WebComponent?, configure: WebDateBoxConfiguration.()->Unit) :WebDateBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var prompt:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false

    private var value:Date? = null
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebDateBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        showClearIcon = configuration.showClearIcon
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
        } catch (e:Throwable){
            return@lambda null
        }
    }

    override fun getHtml(): String {
        return "<input id=\"dateBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun getValue(): Date? {
        if(!initialized){
            return value
        }
        val value = jq.datebox("getText") as String?
        return dateParser(value)
    }

    override fun setValue(value: Date?){
        if(!initialized){
            this.value = value
            return
        }
        jq.datebox("setValue", dateFormatter(value))
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#dateBox$uid")
        var icons = arrayListOf<Any>()
        if(showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.datebox("setValue", null)
                    jq.datebox("getIcon",0).css("visibility","hidden")
                }
            })
        }
        jq.datebox(object{
            val closeText = "Закрыть"
            val currentText = "Сегодня"
            val formatter = dateFormatter
            val parser = { value:String? ->
                dateParser(value)?:Date()
            }
            val icons = icons.toTypedArray()
            val onChange = {newValue:String?,_:String? ->
                jq.datebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(newValue)) "hidden" else "visible")
            }
        })
        val tb = jq.datebox("textbox")
        val c = jq.datebox("calendar")
        c.calendar(object {
            val firstDay = 1
            val months = js("['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек']")
            val weeks = js("['Вс', 'П', 'В', 'С', 'Ч', 'П','Сб']")
        })
        tb.on("input") {
            if(showClearIcon){
                val text = jq.datebox("getText") as String?
                jq.datebox("getIcon",0).css("visibility",if(MiscUtilsJS.isBlank(text)) "hidden" else "visible")
            }
        }
        if(showClearIcon && value == null){
            jq.datebox("getIcon",0).css("visibility","hidden")
        }
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}