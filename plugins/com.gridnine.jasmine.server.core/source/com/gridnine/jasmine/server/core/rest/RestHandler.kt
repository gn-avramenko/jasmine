/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.rest

import com.gridnine.jasmine.server.core.model.rest.BaseRestEntity

interface RestHandler<RQ:BaseRestEntity, RP:BaseRestEntity>{
    fun service(request:RQ):RP
}