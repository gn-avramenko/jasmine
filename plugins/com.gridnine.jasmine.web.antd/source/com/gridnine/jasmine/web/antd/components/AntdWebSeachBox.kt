/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebSearchBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBoxConfiguration

class AntdWebSeachBox(configure:WebSearchBoxConfiguration.()->Unit):WebSearchBox,BaseAntdWebUiComponent() {

    private val config = WebSearchBoxConfiguration()

    private var searcher: (suspend (String?) -> Unit)? = null

    private var searchValue:String? = null

    private var enabled = true

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{
            val props = js("{}")
            props.placeholder = config.prompt
            props.allowClear = "true"
            props.style = js("{}")
            ReactFacade.callbackRegistry.get(it).onChange = { event:dynamic ->
                searchValue = event.target.value
            }
            props.onChange = {event:dynamic ->
                ReactFacade.callbackRegistry.get(it).onChange(event)
            }
            ReactFacade.callbackRegistry.get(it).onSearch = { value:String? ->
                if(searcher != null){
                    launch {
                        searcher!!.invoke(value)
                    }
                }
            }
            props.onSearch = {value:String? ->
                ReactFacade.callbackRegistry.get(it).onSearch(value)
            }
            if(config.className != null){
                props.className = config.className
            }
            if(config.width != null){
                props.style.width = config.width
            }
            if(config.height != null){
                props.style.height = config.height
            }
            ReactFacade.createElement(ReactFacade.Search, props)
        }
    }

    override fun setSearcher(value: suspend (String?) -> Unit) {
        searcher = value
    }

    override fun getValue(): String? {
        return searchValue
    }

    override fun setEnabled(value: Boolean) {
        if(enabled != value){
            enabled = value
            maybeRedraw()
        }
    }
}