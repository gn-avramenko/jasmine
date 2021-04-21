/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.storage

import com.gridnine.jasmine.common.reports.model.domain.ReportDescription
import com.gridnine.jasmine.common.reports.model.domain.ReportDescriptionIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler

class ReportDescriptionIndexHandler :IndexHandler<ReportDescription, ReportDescriptionIndex>{
    override val documentClass = ReportDescription::class
    override val indexClass = ReportDescriptionIndex::class

    override fun createIndexes(doc: ReportDescription): List<ReportDescriptionIndex> {
        return arrayListOf(ReportDescriptionIndex().let {
             it.uid = doc.uid
             it.name = doc.name
                it.id = doc.id
             it
            }
        )
    }
}