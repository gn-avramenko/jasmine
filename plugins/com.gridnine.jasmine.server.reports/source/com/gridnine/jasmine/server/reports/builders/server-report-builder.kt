/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.builders

import com.gridnine.jasmine.common.reports.model.misc.GeneratedReport
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReportList

@DslMarker
annotation class ReportBuilder

fun report(build:Report.()->Unit):GeneratedReport{
    val report = Report()
    report.build()
    val result = GeneratedReport()
    result.lists.addAll(report.lists)
    return result
}

@ReportBuilder
class Report(){
    val lists = arrayListOf<GeneratedReportList>()
    fun list(build:ReportList.()->Unit){
        val lst = ReportList()
        lst.build()
        val result = GeneratedReportList()
        result.title = lst.title
        lists.add(result)
    }
}

@ReportBuilder
class ReportList{
    lateinit var title:String
}