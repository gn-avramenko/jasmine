/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode
import org.w3c.dom.Element

interface ReactElement

interface ReactElementWrapper{
    val element:ReactElement
    val ref:dynamic
}


interface AntdReactFacade {
    val render: (Any,Element) ->Unit
    val createElement: (Any,Any) ->ReactElement
    val createElementWithChildren: (Any,Any,Any) ->ReactElement
    val createElementWrapper: (Any,Any) ->ReactElementWrapper
    val createElementWrapperWithChildren: (Any,Any,Any) ->ReactElementWrapper
    val Layout: Any
    val LayoutHeader: Any
    val LayoutFooter: Any
    val LayoutSider: Any
    val LayoutContent: Any
    val Spin:Any
    val Input:Any
    val Menu:Any
    val SubMenu:Any
    val MenuItem:Any
    val Tabs:Any
    val TabPane:Any
    val createProxy: (createCallback:()->ReactElement)->ReactElementWrapper
}

external val ReactFacade: AntdReactFacade = definedExternally


abstract class BaseAntdWebUiComponent:WebNode{
    private var reactElement:ReactElement? = null

    private var elementRef:dynamic = null

    fun getReactElement():ReactElement{
        if(reactElement == null){
            val wrapper  = createReactElementWrapper()
            reactElement = wrapper.element
            elementRef = wrapper.ref
        }
        return reactElement!!
    }

    fun isInitialized():Boolean{
        return reactElement != null
    }

    fun getElementRef():dynamic{
        if(reactElement == null){
            val wrapper  = createReactElementWrapper()
            reactElement = wrapper.element
            elementRef = wrapper.ref
        }
        return  elementRef!!
    }
    abstract fun createReactElementWrapper(): ReactElementWrapper

    fun maybeRedraw(){
        if(isInitialized()){
            getElementRef().current.forceRedraw()
        }
    }
}

fun findAntdComponent(comp:WebNode):BaseAntdWebUiComponent{
    if(comp is BaseAntdWebUiComponent){
        return comp
    }
    if(comp is BaseWebNodeWrapper<*>){
        return findAntdComponent(comp.getNode())
    }
    throw XeptionJS.forDeveloper("unable to find AntdWebUiComponent of $comp")
}