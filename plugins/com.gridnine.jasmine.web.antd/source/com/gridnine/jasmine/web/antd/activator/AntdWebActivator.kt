/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.activator

import com.gridnine.jasmine.web.antd.components.AntdWebUiLibraryAdapter
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter

const val pluginId = "com.gridnine.jasmine.web.antd"


fun main() {
    RegistryJS.get().register(AntdWebActivator())
}


class AntdWebActivator : ActivatorJS {
    override suspend fun activate() {
        EnvironmentJS.publish(WebUiLibraryAdapter::class, AntdWebUiLibraryAdapter())
        console.log("antd web plugin activated")
    }

    override fun getId(): String {
        return pluginId
    }
}