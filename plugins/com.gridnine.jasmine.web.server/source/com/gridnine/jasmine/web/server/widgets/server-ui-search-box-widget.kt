/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiTextBoxConfiguration

class ServerUiSearchBoxWidget(config:ServerUiSearchBoxWidgetConfiguration): BaseServerUiNodeWrapper(){

    private var searchHandler: ((String?) ->Unit)? = null

    init{
        val comp = ServerUiLibraryAdapter.get().createTextBox(ServerUiTextBoxConfiguration{
            width = config.width
            height = config.height
        })
        comp.setActionListener {
            searchHandler?.invoke(it)
        }
        _node = comp
    }

    fun setSearchHandler(handler: (String?) ->Unit){
        searchHandler = handler
    }
}

class ServerUiSearchBoxWidgetConfiguration(){
    constructor(config:ServerUiSearchBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null

}