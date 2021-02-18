/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

interface ServerUiPanel : ServerUiComponent{
    fun setTitle(title:String)
    fun setMaximizeHandler(handler:() ->Unit)
    fun setMinimizeHandler(handler:() ->Unit)
    fun setContent(comp:ServerUiComponent?)
}

class ServerUiPanelConfiguration{
    var width:String? = null
    var height:String? = null
    var maximizable = false
    var minimizable = false
}
