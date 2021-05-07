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
}


class WebMenuItemConfiguration(val id:String):BaseButtonConfiguration()

class WebMenuButtonConfiguration:BaseButtonConfiguration(){
    var width:String?=null
    var height:String?=null
    val items = arrayListOf<WebMenuItemConfiguration>()
}

interface WebMenuButton: WebNode {
    fun setHandler(id:String, handler:()-> Unit)
    fun setEnabled(id:String, value:Boolean)
    fun setEnabled(value:Boolean)
}