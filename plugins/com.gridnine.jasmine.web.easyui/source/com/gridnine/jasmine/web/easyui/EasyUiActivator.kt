/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.core.application.ActivatorJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.mainframe.EasyUiMainFrameImpl
import com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceEditor
import com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceListEditor
import com.gridnine.jasmine.web.easyui.widgets.table.EasyUiEntityTableColumnEditor
import com.gridnine.jasmine.web.easyui.widgets.table.EasyUiEnumTableColumnEditor

class EasyUiActivator : ActivatorJS{

    override fun configure(config: Map<String, Any?>) {
        ReflectionFactoryJS.get().registerClass("com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceListEditor.SortOrderWrapperVMJS") {EasyUiWorkspaceListEditor.SortOrderWrapperVMJS()}
        ReflectionFactoryJS.get().registerClass("com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceListEditor.SortOrderWrapperVSJS") {EasyUiWorkspaceListEditor.SortOrderWrapperVSJS()}
        ReflectionFactoryJS.get().registerClass("com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceListEditor.SortOrderWrapperVVJS") {EasyUiWorkspaceListEditor.SortOrderWrapperVVJS()}
        extendDatagridEditors(EasyUiEnumTableColumnEditor,EasyUiEntityTableColumnEditor)
        EnvironmentJS.publish(ErrorHandler::class, object:ErrorHandler{
            override fun showError(msg: String, stacktrace: String) {
                showError(null, msg, stacktrace)
            }
        })
        EnvironmentJS.publish(UiFactory::class, EasyUiFactory())
        if(MainFrameConfiguration.get().showWorkspaceEditor){
            MainFrameConfiguration.get().addTool(object:MainFrameTool{
                override val displayName: String
                    get() = "Редактор рабочей области"
                override val weight: Double
                    get() = 10.toDouble()

                override fun handle(mainFrame: MainFrame) {
                    (mainFrame as EasyUiMainFrameImpl).openTab(EasyUiWorkspaceEditor())
                }
            })
        }
    }
}