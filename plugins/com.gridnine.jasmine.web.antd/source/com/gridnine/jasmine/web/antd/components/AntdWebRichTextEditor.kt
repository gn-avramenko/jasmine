/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class AntdWebRichTextEditor(private val configure: WebRichTextEditorConfiguration.()->Unit) : WebRichTextEditor, BaseAntdWebUiComponent() {

    private val config = WebRichTextEditorConfiguration()

    private var value = false

    private var disabled = true

    private val id = "rte${MiscUtilsJS.createUUID()}"

    private var content:String? = null

    init {
        config.configure()
    }

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxy(parentIndex) { parentIndexValue:Int?, childIndex:Int ->
            val props = js("{}")
            props.id = id
            props.theme="snow"
            props.defaultValue = content
            props.style = js("{}")
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange = {value:String? ->
                content = value
            }
            props.onChange = {value:String? ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange(value)
            }
            ReactFacade.createElement(ReactFacade.ReactQuill, props)
        }
    }

    override fun setContent(content: String?) {
        if(this.content != content){
            this.content = content
            maybeRedraw()
        }
    }

    override fun getContent(): String? {
       return content
    }

    override fun setDisabled(value: Boolean) {
        if(disabled != value){
            disabled = value
            maybeRedraw()
        }
    }

    override fun getId(): String? {
        return id;
    }


}