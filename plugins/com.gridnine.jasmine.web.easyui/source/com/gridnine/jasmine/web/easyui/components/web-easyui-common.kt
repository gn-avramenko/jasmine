/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode

interface EasyUiComponent: WebNode {
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