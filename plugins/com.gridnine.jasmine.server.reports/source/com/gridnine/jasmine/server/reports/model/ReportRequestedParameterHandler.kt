/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.model

import com.gridnine.jasmine.common.reports.model.misc.ReportRequestedParameterDescription

interface ReportRequestedParameterHandler<T:Any> {

    fun createParameterDescription():ReportRequestedParameterDescription

}