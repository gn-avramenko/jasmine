/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget

class ProxyIntBoxWidget : IntegerBoxWidget() {

    private var value:Int? = null

    private var validation:String? = null

    init {

        setData = {
            value = it
        }
        configure = { _: Unit ->}
        showValidation = {
            validation = it
        }
        getData = {
            value
        }
    }

    fun getValidationValue():String?{
        return validation
    }

}