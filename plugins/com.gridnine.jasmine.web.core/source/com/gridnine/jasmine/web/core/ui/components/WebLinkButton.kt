/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasVisibility
import com.gridnine.jasmine.web.core.ui.WebComponent


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