/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.editor

import com.gridnine.jasmine.common.reports.model.domain.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTabsPosition
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window

@Suppress("UnsafeCastFromDynamic")
class ReportResultPanel:BaseWebNodeWrapper<WebTag>() {

    private val uuid = MiscUtilsJS.createUUID()

    private var reportData:GeneratedReportJS? = null

    private lateinit var contentDiv:WebTag
    init {
        _node = WebUiLibraryAdapter.get().createTag("div", "reportDiv$uuid")
        _node.getStyle().setParameters("width" to "100%", "height" to "100%")
    }

    fun setData(data:GeneratedReportJS, reportId:String){
        reportData = data
        _node.getChildren().clear()
        contentDiv = WebUiLibraryAdapter.get().createTag("div", "reportContentDiv$uuid")
        contentDiv.getStyle().setParameters("width" to "100%", "height" to "100%", "overflow-y" to "auto","overflow-x" to "auto" )
        contentDiv.setPostRenderAction {
            val styleElmId = reportId.toLowerCase()
            if(window.document.getElementById(styleElmId) != null){
                return@setPostRenderAction
            }
            val styleElm = window.document.createElement("style")
            styleElm.setAttribute("id", styleElmId)
            styleElm.appendChild(window.document.createTextNode(""))
            window.document.asDynamic().head.appendChild(styleElm)
            val sheet = styleElm.asDynamic().sheet
            data.styles.forEach { style ->
                sheet.insertRule(""".${reportId}_${style.id} {
                   border-top: ${getBorderWidth(style.topBorderWidth)};
                   border-bottom: ${getBorderWidth(style.bottomBorderWidth)};
                   border-left:${getBorderWidth(style.leftBorderWidth)};
                   border-right:${getBorderWidth(style.rightBorderWidth)};
                   text-align: ${getHorizontalAlign(style)};
                   vertical-align:${getVerticalAlign(style)};
                   font-family: ${getFontFamily(style)};
                   font-style: ${getFontStyle(style)};
                   font-size: ${getFontSize(style)};
                   font-weight:${getFontWeight(style)};
                   text-decoration:${getFontDecoration(style)};
                   ${buildBackground(style)}    
                }""".trimIndent())
            }
            console.log("styles updated")

        }
        val tabs = WebUiLibraryAdapter.get().createTabsContainer {
            width = "100%"
            height = "100%"
            tabsPositions = WebTabsPosition.BOTTOM
        }
        data.lists.forEach {list ->
            tabs.addTab {
                title = list.title
                closable = false
                content = createReportList(list, reportId)
            }
        }
        contentDiv.getChildren().addChild(tabs)
        _node.getChildren().addChild(contentDiv)
    }

    private fun createReportList(list: GeneratedReportListJS, reportId: String): WebNode {
        val tableTag = WebUiLibraryAdapter.get().createTag("table")
        tableTag.getStyle().setParameters("table-layout" to "fixed", "width" to "${list.columns.map { it.width }.reduce{x,y -> x+y}}px", "border-collapse" to "collapse")
        tableTag.getAttributes().setAttributes("border" to "0", "cellspacing" to "0", "cellpadding" to "2")
        val colGroup = WebUiLibraryAdapter.get().createTag("colgroup")
        tableTag.getChildren().addChild(colGroup)
        list.columns.forEach {
            val col = WebUiLibraryAdapter.get().createTag("col")
            colGroup.getChildren().addChild(col)
            col.getAttributes().setAttributes("width" to "${it.width*5}")
        }
        list.rows.withIndex().forEach {(ri, row) ->
            val tr = WebUiLibraryAdapter.get().createTag("tr")
            tableTag.getChildren().addChild(tr)
            tr.getStyle().setParameters("height" to "${row.height ?: 20}px")
            row.cells.withIndex().forEach {(ci, cell) ->
                if(!isSkip(ri, ci, list.mergedRegions)){
                    val td = WebUiLibraryAdapter.get().createTag("td")
                    tr.getChildren().addChild(td)
                    cell.styleId?.let{td.getClass().addClasses("${reportId}_$it")}
                    getSpan(ri, ci, list.mergedRegions)?.let {
                        td.getAttributes().setAttributes("rowspan" to "${it.first}","colspan" to "${it.second}")
                    }
                    val textValue = when(cell.contentType){
                        GeneratedReportCellValueTypeJS.NONE -> null
                        GeneratedReportCellValueTypeJS.NUMBER -> cell.formatedValue
                        GeneratedReportCellValueTypeJS.DATE -> cell.formatedValue
                        GeneratedReportCellValueTypeJS.FORMULA -> cell.formatedValue
                        GeneratedReportCellValueTypeJS.TEXT -> cell.value
                    }
                    if(!MiscUtilsJS.isBlank(textValue)){
                        td.setText(textValue)
                    }
                }
            }
        }
        return tableTag
    }

    fun getData() = reportData


    private fun buildBackground(style: GeneratedReportCellStyleJS): String{
        return when(style.foregroundColor){
            GeneratedReportColorJS.BLACK -> "background-color:black;"
            GeneratedReportColorJS.GREY_40_PERCENT -> "background-color: rgb(100, 100, 100);"
            GeneratedReportColorJS.GREY_25_PERCENT -> "background-color: rgb(180, 180, 180);"
            null -> ""
        }
    }

    private fun getFontDecoration(style: GeneratedReportCellStyleJS): String {
        return when(style.fontUnderline){
            GeneratedReportFontUnderlineJS.SINGLE -> "underline"
            GeneratedReportFontUnderlineJS.DOUBLE -> "underline"
            GeneratedReportFontUnderlineJS.SINGLE_ACCOUNTING -> "underline"
            GeneratedReportFontUnderlineJS.DOUBLE_ACCOUNTING -> "underline"
            GeneratedReportFontUnderlineJS.NONE -> "none"
            null -> "none"
        }
    }

    private fun getFontWeight(style: GeneratedReportCellStyleJS): String {
        return if(style.fontBold == true) "bold" else "normal"
    }

    private fun getFontSize(style: GeneratedReportCellStyleJS): String {
        return "${style.fontHeight?:12}px"
    }

    private fun getFontStyle(style: GeneratedReportCellStyleJS): String {
        return if(style.fontItalic == true) "italic" else "normal"
    }

    private fun getFontFamily(style: GeneratedReportCellStyleJS): String {
        return style.fontFamily?:"sans-serif;"

    }

    private fun getVerticalAlign(style: GeneratedReportCellStyleJS): String {
        return when(style.verticalAlignment){
            GeneratedReportCellVerticalAlignmentJS.TOP -> "top"
            GeneratedReportCellVerticalAlignmentJS.CENTER -> "middle"
            GeneratedReportCellVerticalAlignmentJS.BOTTOM -> "bottom"
            null -> "middle"
        }
    }

    private fun getHorizontalAlign(style: GeneratedReportCellStyleJS): String {
        return when(style.horizontalAlignment){
            GeneratedReportCellHorizontalAlignmentJS.LEFT -> "left"
            GeneratedReportCellHorizontalAlignmentJS.CENTER -> "center"
            GeneratedReportCellHorizontalAlignmentJS.RIGHT -> "right"
            null -> "center"
        }
    }

    private fun getBorderWidth(topBorderWidth: GeneratedReportCellBorderWidthJS?): String {
        return when(topBorderWidth){
            GeneratedReportCellBorderWidthJS.NONE -> "0px"
            GeneratedReportCellBorderWidthJS.THIN -> "1px solid black"
            GeneratedReportCellBorderWidthJS.THICK -> "2px solid black"
            null -> "0px"
        }
    }


    private fun isSkip(ri: Int, ci: Int, mergedRegions: List<GeneratedReportMergeRegionJS>): Boolean {
        return mergedRegions.any { !(it.leftTopRow == ri && it.leftTopColumn ==ci) && (ri >= it.leftTopRow && ri <= it.rightBottomRow)
                && (ci >= it.leftTopColumn && ci <= it.rightBottomColumn) }
    }

    private fun getSpan(ri: Int, ci: Int, mergedRegions: List<GeneratedReportMergeRegionJS>):Pair<Int,Int>?{
        return mergedRegions.find { (it.leftTopRow == ri && it.leftTopColumn ==ci) && (ri >= it.leftTopRow && ri <= it.rightBottomRow) }?.
        let{
            Pair(it.rightBottomRow-it.leftTopRow + 1, it.rightBottomColumn-it.leftTopColumn + 1)
        }
    }
}