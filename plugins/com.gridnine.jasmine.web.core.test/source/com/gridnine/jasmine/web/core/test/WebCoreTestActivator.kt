/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.test

import com.gridnine.jasmine.web.core.activator.WebCoreActivator
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.RpcManager

class WebCoreTestActivator: ActivatorJS {
    override suspend fun activate() {
        EnvironmentJS.publish(RpcManager::class, TestRpcManager())
    }

    override fun getId(): String {
        return WebCoreTestActivator::class.simpleName!!
    }
}