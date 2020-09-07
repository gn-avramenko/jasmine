/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


abstract class BaseButtonConfiguration{
    var title:String? = null
    var icon:String? = null
}

class LinkButtonConfiguration:BaseButtonConfiguration(){
    lateinit var handler:()->Unit
}