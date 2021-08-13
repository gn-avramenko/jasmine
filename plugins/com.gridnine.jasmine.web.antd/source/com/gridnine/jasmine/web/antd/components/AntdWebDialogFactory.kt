/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.Dialog
import com.gridnine.jasmine.web.core.ui.components.DialogConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebNode
import kotlinx.browser.document
import org.w3c.dom.get

object AntdWebDialogFactory {
    private const val dialogElmId ="jasmineModalDialog"
    fun <W : WebNode> showDialog(dialogContent: W, configure: DialogConfiguration<W>.() -> Unit): Dialog<W> {
        val bodyElm = document.getElementsByTagName("body").get(0).asDynamic()
        val config = DialogConfiguration<W>()
        config.configure()
        val dialogElm = document.getElementById(dialogElmId)?: kotlin.run {
            val elm= document.createElement("div")
            elm.setAttribute("id", dialogElmId)
            bodyElm.appendChild(elm)
            elm
        }
        val props = js("{}")

        props.destroyOnClose = true
        props.title = config.title
        props.visible = true
        props.closable = true
        if(config.expandToMainFrame){
            val dialogWidth = bodyElm.offsetWidth as Int - 100
            val dialogHeight = bodyElm.offsetHeight as Int - 200
            props.style = js("{}")
            props.style.width = dialogWidth
            props.style.height = dialogHeight
            props.width = dialogWidth
        }

        lateinit var dialog: Dialog<W>
        val reactElement = ReactFacade.createProxyAdvanced({ idx ->
            val calbacks= ReactFacade.callbackRegistry.get(idx)
            calbacks.onCancelIcon = {
                ReactFacade.render(ReactFacade.createElement(ReactFacade.Fragment, object{}), dialogElm)
            }
            props.onCancel = {
                ReactFacade.callbackRegistry.get(idx).onCancelIcon()
            }
            props.footer = config.buttons.withIndex().map{(buttonIdx, button) ->
                val buttonProps = js("{}")
                val functionName = "onButton$buttonIdx"
                calbacks[functionName] = {
                    launch {
                        button.handler.invoke(dialog)
                    }
                }
                buttonProps.onClick = {
                    ReactFacade.callbackRegistry.get(idx)[functionName]()
                }
                ReactFacade.createElementWithChildren(ReactFacade.Button,buttonProps, button.displayName)
            }.toTypedArray()
            ReactFacade.createElementWithChildren(ReactFacade.Modal, props, arrayOf(findAntdComponent(dialogContent).getReactElement()))
        }, object{
            val componentDidMount = {
                if(!config.expandToMainFrame){
                    val containerElm = document.asDynamic().querySelector(".ant-modal")
                    val dialogBodyElm = document.asDynamic().querySelector(".ant-modal-body")
                    val contentElm = dialogBodyElm.querySelector("div")
                    containerElm.style.width = "${contentElm.offsetWidth+40}px"
                }
            }
        })
        ReactFacade.render(reactElement.element, dialogElm)
        dialog = object :Dialog<W>{
            override fun close() {
                ReactFacade.render(ReactFacade.createElement(ReactFacade.Fragment, object{}), dialogElm)
            }

            override fun getContent(): W {
                return dialogContent
            }

            override suspend fun simulateClick(buttonIdx: Int): Any? {
                return config.buttons[buttonIdx].handler.invoke(dialog)
            }

        }
        return dialog
    }
}