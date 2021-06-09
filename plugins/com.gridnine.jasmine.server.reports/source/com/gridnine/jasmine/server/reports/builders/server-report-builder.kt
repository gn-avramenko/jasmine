/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.server.reports.builders

import com.gridnine.jasmine.common.reports.model.domain.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val DATE_STORE_FORMATTER:DateTimeFormatter  = DateTimeFormatter.BASIC_ISO_DATE


@DslMarker
annotation class ReportBuilder

fun report(build:Report.()->Unit):GeneratedReport{
    val report = Report()
    report.build()
    val result = GeneratedReport()
    result.lists.addAll(report.lists)
    result.fileName = report.fileName
    result.styles.addAll(report.styles)
    return result
}

@ReportBuilder
class Report {
    private var styleIndex:Int = 0
    internal val lists = arrayListOf<GeneratedReportList>()
    internal val styles = arrayListOf<GeneratedReportCellStyle>()


    lateinit var fileName:String
    fun list(build:ReportList.()->Unit){
        val lst = ReportList()
        lst.build()
        val result = GeneratedReportList()
        result.uid = ""
        result.title = lst.title
        result.columns.addAll(lst.colls)
        result.mergedRegions.addAll(lst.mergedRegions)
        result.rows.addAll(lst.rows)
        lists.add(result)
    }

    fun style(build:Style.()->Unit):GeneratedReportCellStyle{
        styleIndex++
        val hStyle = Style()
        hStyle.build()
        val result = GeneratedReportCellStyle()
        result.uid = ""
        result.id = "reportStyle$styleIndex"
        hStyle.parentStyle?.let {
            result.bottomBorderWidth =it.bottomBorderWidth
            result.leftBorderWidth =it.leftBorderWidth
            result.rightBorderWidth =it.rightBorderWidth
            result.topBorderWidth =it.topBorderWidth
            result.horizontalAlignment =it.horizontalAlignment
            result.verticalAlignment =it.verticalAlignment
            result.wrapText =it.wrapText
            result.fontFamily =it.fontFamily
            result.fontHeight =it.fontHeight
            result.fontBold =it.fontBold
            result.fontItalic =it.fontItalic
            result.fontUnderline =it.fontUnderline
            result.format =it.format
            result.locked =it.locked
            result.fontColor =it.fontColor
            result.foregroundColor =it.foregroundColor
        }
        hStyle.bottomBorderWidth?.let { result.bottomBorderWidth = it }
        hStyle.leftBorderWidth?.let { result.leftBorderWidth = it }
        hStyle.rightBorderWidth?.let { result.rightBorderWidth = it }
        hStyle.topBorderWidth?.let { result.topBorderWidth = it }
        hStyle.verticalAlignment?.let { result.verticalAlignment = it }
        hStyle.horizontalAlignment?.let { result.horizontalAlignment = it }
        hStyle.wrapText?.let { result.wrapText = it }
        hStyle.fontHeight?.let { result.fontHeight = it }
        hStyle.fontItalic?.let { result.fontItalic = it }
        hStyle.format?.let { result.format = it }
        hStyle.fontBold?.let { result.fontBold = it }
        hStyle.locked?.let { result.locked = it }
        hStyle.fontColor?.let { result.fontColor = it }
        hStyle.foregroundColor?.let { result.foregroundColor = it }
        styles.add(result)
        return result
    }


}

@ReportBuilder
class Columns {
    internal val columnsWidths = arrayListOf<Int>()
    fun column(value:Int){
        columnsWidths.add(value)
    }
}
@ReportBuilder
class Style {

    var parentStyle:GeneratedReportCellStyle? = null

    var bottomBorderWidth:GeneratedReportCellBorderWidth?=null

    var leftBorderWidth:GeneratedReportCellBorderWidth?=null

    var rightBorderWidth:GeneratedReportCellBorderWidth?=null

    var topBorderWidth:GeneratedReportCellBorderWidth?=null

    var horizontalAlignment:GeneratedReportCellHorizontalAlignment?=null

    var verticalAlignment:GeneratedReportCellVerticalAlignment?=null

    var wrapText:Boolean?=null

    var fontFamily:String?=null

    var fontHeight:Int?=null

    var fontBold:Boolean?=null

    var fontItalic:Boolean?=null

    var fontUnderline:GeneratedReportFontUnderline?=null

    var format:String?=null

    var locked:Boolean?=null

    var fontColor:GeneratedReportColor?=null

    var foregroundColor:GeneratedReportColor?=null

}

@ReportBuilder
class ReportList{
    lateinit var title:String
    internal val colls = arrayListOf<GeneratedReportColumn>()
    internal val mergedRegions = arrayListOf<GeneratedReportMergeRegion>()
    internal val rows = arrayListOf<GeneratedReportRow>()
    private var currentRowIndex:Int = -1
    private var currentColumnIndex:Int = -1
    private lateinit var currentRow:GeneratedReportRow
    private lateinit var currentCell: GeneratedReportCell
    var defaultRowHeight = 10
    private val numberFormattersCache = hashMapOf<String?, DecimalFormat>()
    private val dateFormattersCache = hashMapOf<String?, DateTimeFormatter>()

    fun columns(build:Columns.()->Unit){
        val hColumns = Columns()
        hColumns.build()
        colls.addAll(hColumns.columnsWidths.map { coll -> GeneratedReportColumn().let {
            it.uid = ""
            it.width = coll
            it
        } })
    }

    fun getCurrentRowIndex():Int{
        return currentRowIndex
    }

    fun row(rowHeight:Int? =null) {
        currentRow = GeneratedReportRow()
        currentRow.uid = ""
        currentRow.height = rowHeight?:defaultRowHeight
        rows.add(currentRow)
        currentRowIndex++
        currentColumnIndex = -1
    }

    fun emptyCell(style:GeneratedReportCellStyle? = null, hSpan:Int = 1, vSpan:Int = 1){
        currentColumnIndex++
        val cell = GeneratedReportCell()
        cell.uid = ""
        currentRow.cells.add(cell)
        cell.contentType = GeneratedReportCellValueType.NONE
        cell.styleId = style?.id
        addMergedRegion(hSpan, vSpan)
    }

    fun number(value:BigDecimal?, style:GeneratedReportCellStyle? = null, hSpan:Int = 1, vSpan:Int = 1){
        if(value == null){
            emptyCell(style, hSpan, vSpan)
            return
        }
        currentColumnIndex++
        val cell = GeneratedReportCell()
        cell.uid = ""
        currentRow.cells.add(cell)
        cell.contentType = GeneratedReportCellValueType.NUMBER
        cell.styleId = style?.id
        cell.value = value.toPlainString()
        cell.formatedValue = formatNumber(style, value)
        addMergedRegion(hSpan, vSpan)
    }

    fun text(value:String?, style:GeneratedReportCellStyle? = null, hSpan:Int = 1, vSpan:Int = 1){
        if(value == null){
            emptyCell(style, hSpan, vSpan)
            return
        }
        currentColumnIndex++
        val cell = GeneratedReportCell()
        cell.uid = ""
        currentRow.cells.add(cell)
        cell.contentType = GeneratedReportCellValueType.TEXT
        cell.styleId = style?.id
        cell.value = value
        addMergedRegion(hSpan, vSpan)
    }

    fun date(value:LocalDate?, style:GeneratedReportCellStyle? = null, hSpan:Int = 1, vSpan:Int = 1){
        if(value == null){
            emptyCell(style, hSpan, vSpan)
            return
        }
        currentColumnIndex++
        val cell = GeneratedReportCell()
        cell.uid = ""
        currentRow.cells.add(cell)
        cell.contentType = GeneratedReportCellValueType.DATE
        cell.styleId = style?.id
        cell.value = DATE_STORE_FORMATTER.format(value)
        cell.formatedValue = formatDate(style, value)
        addMergedRegion(hSpan, vSpan)
    }

    fun numberFormula(value:String?, calculatedValue:BigDecimal?, style:GeneratedReportCellStyle? = null, hSpan:Int = 1, vSpan:Int = 1){
        if(value == null){
            emptyCell(style, hSpan, vSpan)
            return
        }
        currentColumnIndex++
        val cell = GeneratedReportCell()
        cell.uid = ""
        currentRow.cells.add(cell)
        cell.contentType = GeneratedReportCellValueType.FORMULA
        cell.styleId = style?.id
        cell.value =calculatedValue?.toPlainString()
        cell.formatedValue = formatNumber(style, calculatedValue)
        cell.formula = value
        addMergedRegion(hSpan, vSpan)
    }

    private fun addMergedRegion(hSpan: Int, vSpan: Int) {
        if(hSpan ==1 && vSpan == 1){
            return
        }
        mergedRegions.add(GeneratedReportMergeRegion().let {
            it.uid = ""
            it.leftTopRow = currentRowIndex
            it.leftTopColumn = currentColumnIndex
            it.rightBottomRow = currentRowIndex+vSpan-1
            it.rightBottomColumn = currentColumnIndex+hSpan-1
            it
        })
    }

    private fun formatDate(style: GeneratedReportCellStyle?, date: LocalDate?): String? {
        if(date == null){
            return null
        }
        val format = dateFormattersCache.getOrPut(style?.id?:""){
            style?.format?.let {  DateTimeFormatter.ofPattern(it)}?:DateTimeFormatter.ofPattern("yyyy.MM.dd")
        }
        return format.format(date)
    }


    private fun formatNumber(style: GeneratedReportCellStyle?, value: Number?): String? {
        if(value == null){
            return null
        }
        val format = numberFormattersCache.getOrPut(style?.id?:""){
            style?.format?.let {  DecimalFormat(it) }?: DecimalFormat("#")
        }
        return format.format(value)
    }


}