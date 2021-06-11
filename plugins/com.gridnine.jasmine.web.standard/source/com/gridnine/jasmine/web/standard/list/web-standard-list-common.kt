/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.list

import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.standard.model.rest.BaseListFilterValueDTJS
import com.gridnine.jasmine.web.core.ui.components.WebNode
import kotlin.reflect.KClass


internal interface ListFilterHandler<V : BaseListFilterValueDTJS, W : WebNode> {
    fun createEditor(): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

interface ListLinkButtonHandler<E:BaseIdentityJS> {
    suspend fun invoke(selected:List<E>)
}

interface ListWrapper<E:BaseIdentityJS>:WebNode {
    fun getSelectedItems():List<E>
}