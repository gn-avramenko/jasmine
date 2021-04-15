/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.model

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.standard.model.domain.Workspace

interface WorkspaceProvider{
    fun getWorkspace():Workspace
    fun saveWorkspace(workspace:Workspace):Workspace

    companion object{
        private val wrapper = PublishableWrapper(WorkspaceProvider::class)
        fun get() = wrapper.get()
    }
}
