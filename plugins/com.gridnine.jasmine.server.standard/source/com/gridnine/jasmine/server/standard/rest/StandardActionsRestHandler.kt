/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.ActionDescription
import com.gridnine.jasmine.common.core.meta.ActionsGroupDescription
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class StandardActionsRestHandler :RestHandler<GetActionsRequest,GetActionsResponse>{
    override fun service(request: GetActionsRequest, ctx: RestOperationContext): GetActionsResponse {
        val result = GetActionsResponse()
        processGroupDescription(result.actions, request.groupId)
        return result
    }

    private fun processGroupDescription(actions: ArrayList<BaseActionDescriptionDT>, groupId: String) {
        val groupDescription = (UiMetaRegistry.get().actions[groupId] as ActionsGroupDescription?)?:return
        groupDescription.actionsIds.forEach {actId ->
            when(val action = UiMetaRegistry.get().actions[actId]){
                is ActionDescription ->{
                    val ad = ActionDescriptionDT()
                    ad.id = action.id
                    ad.displayName = action.getDisplayName()!!
                    ad.actionHandler = action.actionHandler
                    if(action.displayHandlerRef != null) {
                        ad.displayHandler = UiMetaRegistry.get().displayHandlers[action.displayHandlerRef]!!.className
                    }
                    actions.add(ad)
                }
                is ActionsGroupDescription ->{
                    val ad = ActionsGroupDescriptionDT()
                    ad.id = action.id
                    ad.displayName = action.getDisplayName()!!
                    actions.add(ad)
                    processGroupDescription(ad.actions, ad.id)
                }
            }
        }
    }

}