/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.utils


import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import kotlin.reflect.KClass

object EntityUtils{

    fun<D:BaseEntity> toReference(doc:D):EntityReference<D>{
        return EntityReference(doc::class as KClass<D>, doc.uid, doc.toString())
    }
}

