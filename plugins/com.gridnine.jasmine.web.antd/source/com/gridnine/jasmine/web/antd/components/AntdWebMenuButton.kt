/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.StandardMenuItem
import com.gridnine.jasmine.web.core.ui.components.WebMenuButton
import com.gridnine.jasmine.web.core.ui.components.WebMenuButtonConfiguration

class AntdWebMenuButton(configure:WebMenuButtonConfiguration.()->Unit):WebMenuButton, BaseAntdWebUiComponent() {

    private val config = WebMenuButtonConfiguration()

    private var visible = true

    private var handlers = hashMapOf<String, suspend ()-> Unit>()

    private var enabledItemsMap = hashMapOf<String, Boolean>()

    private var menuEnabled = true

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{
            if(!visible){
                ReactFacade.createElement(ReactFacade.Fragment, object{})
            }  else {
                val menu = ReactFacade.createElementWithChildren(ReactFacade.Menu,object {
                    val disabled = !menuEnabled
                    val onClick = {event:dynamic ->
                        val key = event.key as String
                        handlers[key]?.let {
                            launch {
                                it.invoke()
                            }
                        }
                    }
                }, config.elements.filter { it is StandardMenuItem }.map {
                    it as StandardMenuItem
                    ReactFacade.createElementWithChildren(ReactFacade.MenuItem, object{
                        val key = it.id
                    },it.title!!)}.toTypedArray())
                val dropdown = ReactFacade.createElementWithChildren(ReactFacade.Dropdown, object {
                    val overlay = menu
                    val placement = "bottomLeft"
                }, ReactFacade.createElementWithChildren(ReactFacade.Button,object{}, config.title!!))
                dropdown
            }
        }
    }

    override fun setVisible(value: Boolean) {
        if(visible != value){
            visible = value
            maybeRedraw()
        }
    }

    override fun setHandler(id: String, handler: suspend () -> Unit) {
        handlers[id] = handler
    }

    override fun setEnabled(id: String, value: Boolean) {
        if(enabledItemsMap[id] !=  value){
            enabledItemsMap[id] =  value
            maybeRedraw()
        }
    }

    override fun setEnabled(value: Boolean) {
        if(menuEnabled != value){
            menuEnabled = value
            maybeRedraw()
        }
    }
}