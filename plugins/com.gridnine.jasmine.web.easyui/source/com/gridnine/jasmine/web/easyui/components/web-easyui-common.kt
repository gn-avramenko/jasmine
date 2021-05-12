/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.components.BaseWebComponentConfiguration
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode

interface EasyUiComponent: WebNode {
    fun getId():String
    fun getHtml():String
    fun decorate()
    fun destroy()
}

external fun createSelect2Option(id:String, text:String?, defaultSelected: Boolean, selected:Boolean)
external var jQuery: dynamic = definedExternally

fun findEasyUiComponent(comp:WebNode):EasyUiComponent{
    if(comp is EasyUiComponent){
        return comp
    }
    if(comp is BaseWebNodeWrapper<*>){
        return findEasyUiComponent(comp.getNode())
    }
    throw XeptionJS.forDeveloper("unable to find EasyUiComponent of ${comp}")
}

fun getSizeAttributes(config:BaseWebComponentConfiguration):String{
    return "${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}"
}

fun getIconClass(iconName:String?) = if(iconName != null) "icon_${iconName.substringBefore(":")}_${iconName.substringAfterLast(":")}" else null