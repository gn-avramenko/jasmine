/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.ui.*

class ProxyTableWidget<T: BaseVMEntityJS, VS: BaseVSEntityJS, VV: BaseVVEntityJS> : TableWidget<T, VS, VV>() {

    private val values = arrayListOf<T>()

    private lateinit var config:TableConfigurationJS<VS>

    private lateinit var validation:List<VV>


    init {

        readData = {
            values.clear()
            values.addAll(it)
        }

        writeData = {
            it.clear()
            it.addAll(values)
        }

        configure ={
            config = it
        }

        showValidation={
            validation = it
        }
    }

    fun getValues() = values

    fun getConfiguration()= config

    fun getValidation() = validation

}