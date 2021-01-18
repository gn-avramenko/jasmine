/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasVisibility
import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise


abstract class BaseButtonConfiguration{
    var title:String? = null
    var icon:String? = null
}

class WebLinkButtonConfiguration:BaseButtonConfiguration(){
    var width:String?=null
    var height:String?=null
}
interface WebLinkButton: WebComponent,HasVisibility {
    fun setHandler(handler:()-> Unit)
    fun setEnabled(value:Boolean)
}

interface TestableLinkButton<T> {
    fun simulateClick():Promise<T>
}

class WebMenuItemConfiguration(val id:String, conf:WebMenuItemConfiguration.()->Unit):BaseButtonConfiguration(){
    init {
        this.conf()
    }
}

class WebMenuButtonConfiguration:BaseButtonConfiguration(){
    var width:String?=null
    var height:String?=null
    val items = arrayListOf<WebMenuItemConfiguration>()
}

interface WebMenuButton: WebComponent,HasVisibility {
    fun setHandler(id:String, handler:()-> Unit)
    fun setEnabled(id:String, value:Boolean)
    fun setEnabled(value:Boolean)
}