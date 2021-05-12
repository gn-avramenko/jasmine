/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components

interface  WebNode

interface HasId {
    fun getId():String
}

interface WebNodeWrapper<T: WebNode>: WebNode {
    fun getNode():T
}

abstract class BaseWebNodeWrapper<T: WebNode>: WebNodeWrapper<T> {
    protected lateinit var _node:T

    override fun getNode(): T {
        return _node
    }
}

abstract class BaseWebComponentConfiguration{
    var className:String? = null
    var width:String? = null
    var height:String? = null
}

enum class WebDataHorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}

interface SimpleActionHandler{
    suspend fun invoke()
}

object DefaultUIParameters{
    var controlWidth = 200
    var controlWidthAsString = "200px"
}