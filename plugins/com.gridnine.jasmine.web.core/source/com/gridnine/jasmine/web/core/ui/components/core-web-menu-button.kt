/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

interface MenuElement

class WebMenuButtonConfiguration():BaseButtonConfiguration(){
    var width:String? = null
    var height:String? = null
    val elements = arrayListOf<MenuElement>()
}

abstract class BaseMenuItem:MenuElement{
    lateinit var id:String
    var title:String? = null
    var image:String? = null
}

class MenuSeparator:MenuElement

class StandardMenuItem():BaseMenuItem()

class GroupMenuItem:BaseMenuItem(){
    val children = arrayListOf<MenuElement>()
}

interface WebMenuButton: WebNode {
    fun setVisible(value:Boolean)
    fun setHandler(id:String, handler:suspend ()-> Unit)
    fun setEnabled(id:String, value:Boolean)
    fun setEnabled(value:Boolean)
}