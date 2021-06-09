/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

interface WebTag : WebNode{
    fun getId():String?
    fun getName():String
    fun setText(value:String?)
    fun getChildren():TagChildren
    fun getStyle():TagStyle
    fun getClass():TagClass
    fun getAttributes():TagAttributes
    fun setEventHandler(event:String, handler: (suspend (dynamic) ->Unit)?)
    fun setPostRenderAction(action:(()->Unit)?)
}

interface TagChildren : List<WebNode>{
    fun addChild(child:WebNode)
    fun removeChild(child:WebNode)
    fun clear()
}

interface TagStyle : Map<String, String>{
    fun setParameters(vararg params:Pair<String,String>)
    fun removeParameters(vararg params:String)
}

interface TagAttributes : Map<String, String>{
    fun setAttributes(vararg attrs:Pair<String,String>)
    fun removeAttributes(vararg attrs:String)
}

interface TagClass {
    fun addClasses(vararg classes:String)
    fun removeClasses(vararg classes:String)
}