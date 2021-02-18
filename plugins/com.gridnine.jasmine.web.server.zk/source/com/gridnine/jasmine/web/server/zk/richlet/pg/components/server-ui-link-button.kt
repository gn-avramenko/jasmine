/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent


interface ServerUiLinkButton: ServerUiComponent {
    fun setHandler(handler:()-> Unit)
    fun setEnabled(value:Boolean)
}

class ServerUiLinkButtonConfiguration{
    var title:String? = null
    var width:String?=null
    var height:String?=null
}