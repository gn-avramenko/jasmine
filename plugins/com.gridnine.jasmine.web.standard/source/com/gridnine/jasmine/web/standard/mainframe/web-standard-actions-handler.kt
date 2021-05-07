/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

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
}

class ActionWrapper:BaseActionWrapper(){
    internal lateinit var actionHandlerClassName:String
    private var actionHandler:Any? = null
    suspend fun<E:Any> getActionHandler():E{
        if(actionHandler == null){
            if(!ReflectionFactoryJS.get().isRegistered(actionHandlerClassName)){
                WebPluginsHandler.get().loadPluginForClass(actionHandlerClassName)
            }
            actionHandler = ReflectionFactoryJS.get().getFactory(actionHandlerClassName).invoke()

        }
        return actionHandler as E
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
                action.displayName = it.displayName
                result.actions.add(action)
                return@forEach
            }
            it as ActionsGroupDescriptionDTJS
            val groupWrapper = ActionsGroupWrapper()
            groupWrapper.id = it.id
            groupWrapper.displayName = it.displayName
            result.actions.add(groupWrapper)
            processGroup(groupWrapper, it.actions)
        }
    }

    companion object{
        fun get() = EnvironmentJS.getPublished(WebActionsHandler::class)
    }
}