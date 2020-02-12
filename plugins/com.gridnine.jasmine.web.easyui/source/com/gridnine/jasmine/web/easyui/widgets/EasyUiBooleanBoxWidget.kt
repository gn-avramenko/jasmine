/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.BooleanBoxDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.BooleanBoxWidget
import com.gridnine.jasmine.web.core.model.ui.DateBoxWidget
import com.gridnine.jasmine.web.core.model.ui.DateboxDescriptionJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery
import kotlin.js.Date

class EasyUiBooleanBoxWidget(uid: String, description: BooleanBoxDescriptionJS) : BooleanBoxWidget() {
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized: Boolean = false

    private var value = false
    init {

        setData = {
            div.switchbutton("setValue", it == true)
        }
        configure = { _: Unit ->
            if (!initialized) {
                div.switchbutton(object {
                    val onText = "Да"
                    val offText = "Нет"
                    val onChange = {cv:Boolean ->
                        value = cv
                    }
                })
                initialized = true
            }
        }
        showValidation = {
            Unit
        }
        getData = {
           value
        }
    }

}