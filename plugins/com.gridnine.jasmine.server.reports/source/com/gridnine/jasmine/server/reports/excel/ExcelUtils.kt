/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: coralina-docs-flow
 *****************************************************************/
package com.gridnine.jasmine.server.reports.excel

import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


object ExcelUtils {
    private val numberFormat = DecimalFormat("#.#")

    private fun getCell(sheet:Sheet, row: Int, column: Int, prohibitNull: Boolean): Cell? {
        val sheetName = sheet.sheetName
        val aRow = sheet.getRow(row) ?: run {
            if (prohibitNull) {
                throw Xeption.forEndUser(L10nMessage("отсутствует ряд с индексом ${row + 1} в листе $sheetName"))
            }
            return null
        }
        val cell = aRow.getCell(column) ?: run {
            if (prohibitNull) {
                throw Xeption.forEndUser(
                    L10nMessage(
                        "отсутствует значение в ячейке ${
                            getCellName(
                                row,
                                column
                            )
                        } в листе $sheetName"
                    )
                )
            }
            return null
        }
        if (cell.cellType === CellType.BLANK) {
            if (prohibitNull) {
                throw Xeption.forEndUser(
                    L10nMessage(
                        "отсутствует значение в ячейке ${
                            getCellName(
                                row,
                                column
                            )
                        } в листе $sheetName"
                    )
                )
            }
            return null
        }
        return cell
    }
    fun getStringValue(sheet:Sheet, row: Int, column: Int, prohibitNull: Boolean): String? {
        val sheetName = sheet.sheetName
        val cell = getCell(sheet, row, column, prohibitNull)?:return null
        if (cell.cellType === CellType.STRING) {
            return trim(cell.stringCellValue)
        }
        if (cell.cellType === CellType.NUMERIC) {
            return numberFormat.format(cell.numericCellValue)
        }
        throw Xeption.forEndUser(
            L10nMessage(
            """значение в ячейке ${getCellName(row,column)}  в листе $sheetName не является ни строкой ни числом"""))
    }

    fun getNumberValue(sheet: Sheet, row: Int, column: Int, precision:Int?, dontRiseException: Boolean): BigDecimal? {
        val sheetName = sheet.sheetName
        val cell = getCell(sheet, row, column, false)?: return null
        if (cell.cellType === CellType.STRING) {
            return try {
                if(precision != null) BigDecimal(cell.stringCellValue).setScale(precision, RoundingMode.HALF_EVEN) else BigDecimal(cell.stringCellValue)
            } catch (e: Exception) {
                if (dontRiseException) {
                    return null
                }
                throw Xeption.forEndUser(
                    L10nMessage(
                        """значение в ячейке ${getCellName(row,column)}  в листе $sheetName не удается привести к числу"""))
            }
        }
        if (cell.cellType === CellType.NUMERIC) {
            return                 if(precision != null) cell.numericCellValue.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN) else cell.numericCellValue.toBigDecimal()
        }
        throw Xeption.forEndUser(
            L10nMessage(
                """значение в ячейке ${getCellName(row,column)}  в листе $sheetName не является ни строкой ни числом"""))
    }

    private fun getCellName(row: Int, column: Int): String {
        val colName: Char = ('A'.toInt() + column).toChar()
        return "" + colName + (row + 1)
    }

    fun getCellIndex(position:String):Int{
        return (position[0].toInt() - 'A'.toInt())
    }

    private fun trim(value: String?): String? {
        return value?.trim() ?: value
    }

}