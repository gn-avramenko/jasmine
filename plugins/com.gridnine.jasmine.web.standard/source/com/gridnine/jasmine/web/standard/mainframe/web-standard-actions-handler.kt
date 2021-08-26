/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.standard.mainframe

import com.gridnine.jasmine.common.standard.model.rest.ActionDescriptionDTJS
import com.gridnine.jasmine.common.standard.model.rest.ActionsGroupDescriptionDTJS
import com.gridnine.jasmine.common.standard.model.rest.BaseActionDescriptionDTJS
import com.gridnine.jasmine.common.standard.model.rest.GetActionsRequestJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.WebPluginsHandler
import com.gridnine.jasmine.web.standard.StandardRestClient

abstract class BaseActionWrapper{
    lateinit var id:String
    lateinit var displayName:String
    var icon:String?=null
}

class ActionWrapper:BaseActionWrapper(){
    internal lateinit var actionHandlerClassName:String
    internal var displayHandlerClassName:String? = null
    private var actionHandler:Any? = null
    private var displayHandler:Any? = null
    private var displayHandlerLoaded = false
    suspend fun<E:Any> getActionHandler():E{
        if(actionHandler == null){
            if(!ReflectionFactoryJS.get().isRegistered(actionHandlerClassName)){
                WebPluginsHandler.get().loadPluginForId(actionHandlerClassName)
            }
            actionHandler = ReflectionFactoryJS.get().getFactory(actionHandlerClassName).invoke()

        }
        return actionHandler as E
    }
    suspend fun<E:Any> getDisplayHandler():E?{
        if(!displayHandlerLoaded){
            if(displayHandlerClassName != null){
                if(!ReflectionFactoryJS.get().isRegistered(displayHandlerClassName!!)){
                    WebPluginsHandler.get().loadPluginForId(displayHandlerClassName!!)
                }
                displayHandler = ReflectionFactoryJS.get().getFactory(displayHandlerClassName!!).invoke()
            }
            displayHandlerLoaded = true
        }
        return displayHandler as E?
    }
}

class ActionsGroupWrapper:BaseActionWrapper(){
    val actions = arrayListOf<BaseActionWrapper>()
}
class WebActionsHandler{

    private val cache = hashMapOf<String, ActionsGroupWrapper>()

    suspend fun getActionsFor(group:String):ActionsGroupWrapper{
        return cache.getOrPut(group){
            val result = ActionsGroupWrapper();
            result.id = group
            processGroup(result, StandardRestClient.standard_standard_getActions(GetActionsRequestJS().apply {
                groupId = group
            }).actions)
            result
        }
    }

    private fun processGroup(result: ActionsGroupWrapper, actions: ArrayList<BaseActionDescriptionDTJS>) {
        actions.forEach {
            if(it is ActionDescriptionDTJS){
                val action = ActionWrapper()
                action.id = it.id
                action.actionHandlerClassName = it.actionHandler
                action.displayHandlerClassName = it.displayHandler
                action.displayName = it.displayName
                action.icon = it.icon
                result.actions.add(action)
                return@forEach
            }
            it as ActionsGroupDescriptionDTJS
            val groupWrapper = ActionsGroupWrapper()
            groupWrapper.id = it.id
            groupWrapper.displayName = it.displayName
            groupWrapper.icon = it.icon
            result.actions.add(groupWrapper)
            processGroup(groupWrapper, it.actions)
        }
    }

    companion object{
        fun get() = EnvironmentJS.getPublished(WebActionsHandler::class)
    }
}