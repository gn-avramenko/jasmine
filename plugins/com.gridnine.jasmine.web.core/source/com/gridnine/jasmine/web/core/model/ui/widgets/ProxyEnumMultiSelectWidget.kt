/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*
import kotlin.js.Date

class ProxyEnumMultiSelectWidget<E:Enum<E>> : EnumMultiSelectWidget<E>() {

    private lateinit var values : List<E>

    private var validation:String? = null

    private lateinit var configuration:EnumSelectConfigurationJS<E>

    init {

        readData = {
            values = it
        }
        configure = { config: EnumSelectConfigurationJS<E> -> configuration = config}
        showValidation = {
            validation = it
        }
        writeData = {
            it.clear()
            it.addAll(values)
        }
    }

    fun getValidationValue():String?{
        return validation
    }

    fun getConfiguration() = configuration

}