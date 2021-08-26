/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


abstract class BaseButtonConfiguration:BaseWebComponentConfiguration(){
    var title:String? = null
    var icon:String? = null
    var toolTip:String? = null
}

class WebLinkButtonConfiguration:BaseButtonConfiguration()

interface WebLinkButton: WebNode {
    fun setHandler(handler:suspend ()-> Unit)
    fun setEnabled(value:Boolean)
    fun setVisible(value:Boolean)
}


