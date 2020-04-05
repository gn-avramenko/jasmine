/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponseJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.utils.EasyUiViewBuilder
import com.gridnine.jasmine.web.easyui.widgets.EasyUiEditorButtonWidget
import kotlin.js.Promise

data class EditorTabHandlerData(val content: String, val data: GetEditorDataResponseJS)

class EasyUiEditorTabHandler(private val type: String, private var objectUid: String?, private var navigationKey: String?) : EasyUiTabHandler<EditorTabHandlerData, Editor<*, *, *, *>> {

    private val descriptions = hashMapOf<BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>, BaseToolButtonDescriptionJS>()
    private val toolButtonHandlers = arrayListOf<BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>>()
    private val descr = UiMetaRegistryJS.get().editors.values.find { it.entityId == type }
            ?: throw IllegalArgumentException("unable to find description for editor $type")
    private val viewDescription = UiMetaRegistryJS.get().views[descr.viewId]
            ?: throw IllegalArgumentException("unable to find view for id ${descr.viewId}")

    private val editor = Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>()

    private lateinit var widgets: MutableMap<BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>, ToolButtonWidget>

    init {
        UiMetaRegistryJS.get().sharedEditorToolButtons.forEach {
            val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as BaseSharedEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>
            if (handler.isApplicableToObject(type)) {
                toolButtonHandlers.add(handler)
                descriptions[handler] = it
            }
        }

        descr.toolButtons.forEach {
            val handler = ReflectionFactoryJS.get().getFactory(it.handler)() as BaseEditorToolButtonHandler<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>
            toolButtonHandlers.add(handler)
            descriptions[handler] = it

        }
        toolButtonHandlers.sortBy { descriptions[it]!!.weight }
    }

    override fun getId(): String {
        return "${type}_${objectUid}"
    }

    override fun getData(uid: String): Promise<EditorTabHandlerData> {
        val request = GetEditorDataRequestJS()
        request.objectId = type
        request.objectUid = objectUid


        return Promise { resolve, _ ->
            val contentPromise = Promise<String> { resolveContent, _ ->

                val viewPromise = Promise<String> { resolveView, _ ->
                    when (viewDescription) {
                        is StandardViewDescriptionJS -> {
                            when (val layout = viewDescription.layout) {
                                is TableLayoutDescriptionJS -> {
                                    resolveView(HtmlUtilsJS.html {
                                        div(id = "contentPane${uid}", style = "width:100%;height:100%") {
                                            div(id = "mainPane${uid}", style = "width:100%;height:100%") {
                                                EasyUiViewBuilder.generateHtml(viewDescription.id, uid, false, this)
                                            }
                                        }

                                    }.toString())
                                }
                                else -> throw IllegalArgumentException("unsupported layout type: ${layout::class.simpleName}")
                            }
                        }
                        else -> throw IllegalArgumentException("unsupported view type: ${viewDescription::class.simpleName}")
                    }
                }
                viewPromise.then { viewContent ->
                    val content = HtmlUtilsJS.html {
                        div(`class` = "easyui-layout", data_options = "fit:true") {
                            div(region = "north", border = false, `class` = "group wrap header", style = "height:45px;font-size:100%;padding:5px") {
                                div(`class` = "content") {
                                    div(id = "$uid-buttons", style = "float:left") {
                                        toolButtonHandlers.forEach { handler ->
                                            val description = descriptions[handler]!!
                                            a(href = "#", id = "${description.id}${uid}") {
                                                description.displayName()
                                            }
                                        }
                                    }
                                }
                            }
                            div(id = "$uid-panel", region = "center", border = false) {
                                text(viewContent)
                            }
                        }
                    }.toString()
                    resolveContent(content)
                }
            }
            Promise.all(arrayOf(contentPromise, StandardRestClient.standard_standard_getEditorData(request)))
                    .then { array ->
                        val result = EditorTabHandlerData(array[0] as String, array[1] as GetEditorDataResponseJS)
                        resolve(result)
                    }
        }
    }

    override fun getTitle(data: EditorTabHandlerData): String {
        return data.data.title
    }

    override fun getContent(data: EditorTabHandlerData, uid: String): String {
        return data.content
    }

    private fun updateToolsVisibility() {
        toolButtonHandlers.forEach {
            widgets[it]!!.setEnabled(it.isEnabled(editor))
        }
    }

    override fun decorateData(data: EditorTabHandlerData, uid: String, setTitle: (String) -> Unit, close: () -> Unit): Editor<*, *, *, *> {
        val view = EasyUiViewBuilder.createView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(descr.viewId, uid)
        view.configure(data.data.viewSettings)
        view.readData(data.data.viewModel)
        val editor = Editor<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>()
        editor.close = close
        editor.type = type
        editor.setTitle = setTitle
        editor.view = view
        view.parent = editor
        editor.updateToolsVisibility = this::updateToolsVisibility
        widgets = hashMapOf()
        toolButtonHandlers.forEach {
            val descr = descriptions[it]!!
            widgets[it] = EasyUiEditorButtonWidget("${descr.id}${uid}", it, editor)
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
        updateToolsVisibility()
        if (navigationKey != null) {
            view.navigate(navigationKey!!)
        }
        return editor
    }

    override var cachedEditor: Editor<*, *, *, *>? = null

}