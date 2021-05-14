/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


abstract class BaseButtonConfiguration{
    var title:String? = null
    var icon:String? = null
}

class WebLinkButtonConfiguration:BaseButtonConfiguration(){
    var width:String?=null
    var height:String?=null
}
interface WebLinkButton: WebNode {
    fun setHandler(handler:suspend ()-> Unit)
    fun setEnabled(value:Boolean)
    fun setVisible(value:Boolean)
}


