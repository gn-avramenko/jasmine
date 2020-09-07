/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

interface MenuElement

class MenuButtonConfiguration():BaseButtonConfiguration(){
    constructor(configuration:MenuButtonConfiguration.()->Unit):this(){
        configuration.invoke(this)
    }
    val elements = arrayListOf<MenuElement>()
}

abstract class BaseMenuItem:MenuElement{
    var title:String? = null
    var image:String? = null
}

class MenuSeparator:MenuElement

class StandardMenuItem():BaseMenuItem(){
    constructor(configuration:StandardMenuItem.()->Unit):this(){
        configuration.invoke(this)
    }
    lateinit var handler:()->Unit
}

class GroupMenuItem:BaseMenuItem(){
    val children = arrayListOf<MenuElement>()
}
