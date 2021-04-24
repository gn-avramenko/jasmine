/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.reports.excel

import com.gridnine.jasmine.common.reports.model.misc.GeneratedReport
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReportCellBorderWidth
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReportCellValueType
import com.gridnine.jasmine.common.reports.model.misc.GeneratedReportColor
import com.gridnine.jasmine.server.reports.builders.DATE_STORE_FORMATTER
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.LocalDate


object ExcelGenerator {
    fun generate(data: GeneratedReport):ByteArray{
        val workbook = XSSFWorkbook()
        val stylesMap = hashMapOf<String, XSSFCellStyle>()
        val colorMap = workbook.stylesSource.indexedColors
        data.styles.forEach { rs ->
            val style = workbook.createCellStyle()
            val font = workbook.createFont()
            style.setFont(font)
            rs.fontHeight?.let { font.fontHeightInPoints = it.toShort() }
            rs.fontFamily?.let { font.fontName = it }
            rs.fontItalic?.let { font.italic = it }
            rs.fontUnderline?.let { font.setUnderline(findEnum(FontUnderline.NONE, it.name)) }
            rs.fontBold?.let { font.bold = it }
            style.setFont(font)
            rs.topBorderWidth?.let { style.borderTop = getBorderStyle(it) }
            rs.bottomBorderWidth?.let { style.borderBottom = getBorderStyle(it) }
            rs.leftBorderWidth?.let { style.borderLeft = getBorderStyle(it) }
            rs.rightBorderWidth?.let { style.borderRight = getBorderStyle(it) }
            rs.format?.let {
                style.dataFormat = workbook.createDataFormat().getFormat(it) //$NON-NLS-1$
            }
            rs.locked?.let { style.locked = it }
            rs.horizontalAlignment?.let {
                style.alignment = findEnum(HorizontalAlignment.GENERAL, it.name)
            }
            rs.verticalAlignment?.let {
                style.verticalAlignment = findEnum(VerticalAlignment.CENTER, it.name)
            }
            rs.wrapText?.let { style.wrapText = it }
            rs.foregroundColor?.let {
                style.fillPattern = FillPatternType.SOLID_FOREGROUND
                style.setFillForegroundColor(when (it) {
                    GeneratedReportColor.BLACK -> XSSFColor(Color.BLACK, colorMap)
                    GeneratedReportColor.GREY_40_PERCENT -> XSSFColor(Color.DARK_GRAY, colorMap)
                    GeneratedReportColor.GREY_25_PERCENT -> XSSFColor(Color.LIGHT_GRAY, colorMap)
                })
            }
            stylesMap[rs.id!!] = style
        }
        data.lists.forEach { list ->
            val sheet = workbook.createSheet(list.title)
            list.rows.withIndex().forEach { (ri, row) ->
                val poiRow = sheet.createRow(ri)
                row.height?.let { poiRow.heightInPoints = it.toFloat() }
                row.cells.withIndex().forEach{ (ci, cell) ->
                    val poiCell = poiRow.createCell(ci)
                    poiCell.cellStyle = stylesMap[cell.styleId]
                    when(cell.contentType){
                        GeneratedReportCellValueType.NONE -> poiCell.cellType = CellType.BLANK
                        GeneratedReportCellValueType.NUMBER -> poiCell.setCellValue(BigDecimal(cell.value).toDouble())
                        GeneratedReportCellValueType.DATE -> poiCell.setCellValue(LocalDate.parse(cell.value, DATE_STORE_FORMATTER))
                        GeneratedReportCellValueType.FORMULA -> poiCell.cellFormula = cell.formula
                        GeneratedReportCellValueType.TEXT -> poiCell.setCellValue(cell.value)
                    }
                }
            }
            list.columns.withIndex().forEach { (ci, col) ->
                sheet.setColumnWidth(ci, 256 * col.width)
            }
            list.mergedRegions.forEach {
                sheet.addMergedRegion(CellRangeAddress(it.leftTopRow, it.rightBottomRow, it.leftTopColumn, it.rightBottomColumn))
            }
        }
        val stream = ByteArrayOutputStream()
        workbook.write(stream)
        stream.close()
        return stream.toByteArray()
    }
    private fun getBorderStyle(width: GeneratedReportCellBorderWidth): BorderStyle? {
        return when(width){
            GeneratedReportCellBorderWidth.NONE -> BorderStyle.NONE
            GeneratedReportCellBorderWidth.THIN -> BorderStyle.THIN
            GeneratedReportCellBorderWidth.THICK -> BorderStyle.MEDIUM
        }
    }

    private fun <E : Enum<E>> findEnum(defaultValue: E,
                                       value: String): E {
        for (item in defaultValue.declaringClass.enumConstants) {
            if (item.name == value) {
                return item
            }
        }
        return defaultValue
    }
}