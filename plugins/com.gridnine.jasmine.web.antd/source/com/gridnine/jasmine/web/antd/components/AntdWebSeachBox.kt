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
            val props = object{
                val placeholder = config.prompt
                val allowClear = "true"
                val onChange = { event:dynamic ->
                    searchValue = event.target.value
                }
                val onSearch = {value:String? ->
                    if(searcher != null){
                        launch {
                            searcher!!.invoke(value)
                        }
                    }
                }
                val style = object {}
            }.asDynamic()
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