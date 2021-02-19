/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent

interface SeverUiLabel: ServerUiComponent{
    fun setText(value: String?)
}

class SeverUiLabelConfiguration{
    var width:String?=null
    var height:String?=null
}