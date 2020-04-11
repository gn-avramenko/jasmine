package com.gridnine.jasmine.web.core.test.ui

import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import kotlin.js.Promise

class TestMainFrame : MainFrame {
    override fun openTab(objectId: String, uid: String?, navigationKey: String?): Promise<Editor<*, *, *, *>> {
        return Promise { resolve, reject ->
            val request = GetEditorDataRequestJS()
            request.objectId = objectId
            request.objectUid = uid
            StandardRestClient.standard_standard_getEditorData(request).then {
                val descr = UiMetaRegistryJS.get().editors.values.find { it.entityId == objectId }!!
                val view = TestViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(descr.viewId, uid!!)
                view.configure(it.viewSettings)
                view.readData(it.viewModel)
                val toolButtonHandlers = arrayListOf<BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>>()
                val descriptions = hashMapOf<BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>, BaseToolButtonDescriptionJS>()
                UiMetaRegistryJS.get().sharedEditorToolButtons.forEach {
                    val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as BaseSharedEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>
                    if (handler.isApplicableToObject(objectId)) {
                        toolButtonHandlers.add(handler)
                        descriptions[handler] = it
                    }
                }

                descr.toolButtons.forEach {
                    val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>
                    toolButtonHandlers.add(handler)
                    descriptions[handler] = it
                }


                val editor = Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>()
                toolButtonHandlers.forEach {
                    val descr = descriptions[it]!!
                    if (it is BaseTestableEditorToolButtonHandler<*, *, *, *, *>) {
                        val toolButton = TestableToolButtonWidget<Any>()
                        toolButton.click = {
                            it.onClick(editor.asDynamic()) as Promise<Any>
                        }
                        toolButton.id = descr.id
                        editor.toolButtons.add(toolButton)
                    } else if (it is TestableSharedEditorToolButtonHandler<*, *, *, *, *>) {
                        val toolButton = TestableToolButtonWidget<Any>()
                        toolButton.click = {
                            it.onClick(editor.asDynamic()) as Promise<Any>
                        }
                        toolButton.id = descr.id
                        editor.toolButtons.add(toolButton)
                    } else {
                        val toolButton = ToolButtonWidget()
                        toolButton.id = descr.id
                        editor.toolButtons.add(toolButton)
                    }
                }
                editor.close = {}
                editor.type = objectId
                editor.setTitle = {}
                editor.view = view
                view.parent = editor
                editor.updateToolsVisibility = {}
                resolve(editor)
            }.catch(reject)
        }
    }

}