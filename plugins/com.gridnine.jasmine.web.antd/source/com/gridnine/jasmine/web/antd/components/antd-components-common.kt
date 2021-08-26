/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode
import org.w3c.dom.Element
import kotlin.js.Date

interface ReactElement

interface ReactElementWrapper{
    val element:ReactElement
    val ref:dynamic
}


interface AntdReactFacade {
    val getCallbacks: (Int?, Int) ->dynamic
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
    val incrementAndGetCallbackIndex: ()->Int
    val Spin:Any
    val Input:Any
    val Menu:Any
    val SubMenu:Any
    val MenuItem:Any
    val Tabs:Any
    val TabPane:Any
    val Dropdown:Any
    val Button:Any
    val Fragment:Any
    val Search:Any
    val Table:Any
    val DebounceSelect:Any
    val Select:Any
    val Tooltip:Any
    val IconLinkOutlined:Any
    val SelectOption:Any
    val DatePicker:Any
    val dateToMoment: (Date?) ->Any?
    val momentToDate: (Any?) ->Date?
    val momentToDateTime: (Any?) ->Date?
    val IconEyeTwoTone:Any
    val IconEyeInvisibleOutlined:Any
    val PasswordBox:Any
    val InputNumber:Any
    val notification:dynamic
    val callbackRegistry:dynamic
    val Modal:Any
    val Switch:Any
    val Panel:Any
    val Collapse:Any
    val ReactQuill:Any
    val Tree:Any
    val TreeNode:Any
    val IconCloseOutlined:Any
    val IconPlusOutlined:Any
    val IconUpOutlined:Any
    val IconDownOutlined:Any
    val IconMinusOutlined:Any
    val IconFolderOutlined:Any
    val IconMenuOutlined:Any
    val IconDoubleRightOutlined:Any
    val IconDoubleLeftOutlined:Any
    val IconEditOutlined:Any
    val IconDeleteOutlined:Any
    val IconSaveOutlined:Any
    val IconOrderedListOutlined:Any
    val IconFolderViewOutlined:Any
    val IconExportOutlined:Any
    val createProxyAdvanced: (parentIndex:Int?, createCallback:(Int?, Int)->ReactElement, otherCallbacks:Any?)->ReactElementWrapper
    val createProxy: (parentIndex:Int?, createCallback:(Int?, Int)->ReactElement)->ReactElementWrapper
}

external val ReactFacade: AntdReactFacade = definedExternally


abstract class BaseAntdWebUiComponent:WebNode{
    protected var reactElement:ReactElement? = null

    protected var elementRef:dynamic = null

    private var parentIndex:Int? = null

    fun getReactElement(parentIndex:Int?):ReactElement{
        if(reactElement == null){
            this.parentIndex = parentIndex
            val wrapper  = createReactElementWrapper(parentIndex)
            reactElement = wrapper.element
            elementRef = wrapper.ref
        }
        return reactElement!!
    }

    fun isInitialized():Boolean{
        return reactElement != null
    }

    fun getElementRef(parentIndex: Int?):dynamic{
        if(reactElement == null){
            this.parentIndex = parentIndex
            val wrapper  = createReactElementWrapper(parentIndex)
            reactElement = wrapper.element
            elementRef = wrapper.ref
        }
        return  elementRef!!
    }
    abstract fun createReactElementWrapper(parentIndex: Int?): ReactElementWrapper

    fun maybeRedraw(){
        if(isInitialized()){
            getElementRef(parentIndex).current.forceRedraw()
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