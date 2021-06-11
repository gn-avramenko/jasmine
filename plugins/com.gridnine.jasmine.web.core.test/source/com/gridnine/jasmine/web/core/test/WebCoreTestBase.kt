/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.test

import com.gridnine.jasmine.web.core.activator.WebCoreActivator
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.launchAndHandleException
import kotlin.js.Promise

open class WebCoreTestBase {

    open fun getActivators():MutableList<ActivatorJS>{
        return arrayListOf(WebCoreTestActivator(), WebCoreActivator())
    }

    fun buildBefore(){
        EnvironmentJS.test = true
        before {
            Promise<Unit> {resolve, reject ->
                launchAndHandleException({
                    getActivators().forEach {
                        it.activate()
                    }
                resolve(Unit)
                },
                    reject)
            }
        }
    }
}