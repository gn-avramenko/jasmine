/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.reports.zk.components

import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.reports.model.misc.*
import com.gridnine.jasmine.server.reports.builders.DATE_STORE_FORMATTER
import com.gridnine.jasmine.server.reports.ui.ReportResultPanel
import com.gridnine.jasmine.server.reports.ui.ReportResultPanelConfiguration
import com.gridnine.jasmine.server.zk.ui.components.ZkUiComponent
import com.gridnine.jasmine.server.zk.ui.components.configureBasicParameters
import org.zkoss.zhtml.*
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.*
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

class ZkReportResultPanel(configure: ReportResultPanelConfiguration.() -> Unit) : ReportResultPanel,ZkUiComponent{

    private val numberFormattersCache = hashMapOf<String?, DecimalFormat>()

    private val dateFormattersCache = hashMapOf<String?, DateTimeFormatter>()

    private var component: Tabbox? = null

    private var reportData: GeneratedReport? = null

    private var tabs: Tabs? = null

    private  var tabpanels: Tabpanels? = null

    private val config = ReportResultPanelConfiguration()
    init {
        config.configure()
    }
    override fun setData(data: GeneratedReport) {
        this.reportData = data
        if(component!= null){
            setDataInternal()
        }
    }

    private fun setDataInternal() {
        component!!.getChildren<HtmlBasedComponent>().clear()
        if(reportData != null) {
            val tabs = Tabs()
            this.tabs = tabs
            component!!.appendChild(tabs)
            val panels = Tabpanels()
            this.tabpanels = panels
            component!!.appendChild(panels)
            reportData!!.lists.forEach { list ->
                val tab = Tab()
                tab.isClosable = false
                tab.id = TextUtils.generateUid()
                tab.label = list.title
                tabs.appendChild(tab)

                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                tabbPanel.style="overflow-y: auto;overflow-x: auto;"
                tabpanels!!.appendChild(tabbPanel)
                val zkTable = Table()
                zkTable.style = "table-layout: fixed; width:${list.columns.map { it.width }.reduce{x,y -> x+y}}px;border-collapse: collapse;"
                zkTable.setClientAttribute("border", "0")
                zkTable.setClientAttribute("cellspacing", "0")
                zkTable.setClientAttribute("cellpadding", "2")
                tabbPanel.appendChild(zkTable)
                fillTable(zkTable, list, reportData!!.styles)
                updateStyles(reportData!!.descriptionUid, reportData!!.styles)
            }
            if(reportData!!.lists.isNotEmpty()) {
                component!!.selectedIndex = 0
            }
        }
    }

    private fun updateStyles(descriptionUid:String, styles:List<GeneratedReportCellStyle>){
        val script = """
            jq("#$descriptionUid").remove();
            (function() {
	        var style = document.createElement("style");
            style.setAttribute("id", "$descriptionUid")
	        style.appendChild(document.createTextNode(""));
            document.head.appendChild(style);
            var sheet= style.sheet;
            ${styles.joinToString("\n") { createStyle(it) }}
})();
        """.trimMargin()
        Clients.evalJavaScript(script)
    }

    private fun createStyle(style: GeneratedReportCellStyle):String {
        val builder = StringBuilder()
        builder.append("sheet.insertRule(\".${style.id} {"+
                "border-top: ${getBorderWidth(style.topBorderWidth)};"+
                "border-bottom: ${getBorderWidth(style.bottomBorderWidth)};"+
                "border-left:${getBorderWidth(style.leftBorderWidth)};"+
                "border-right:${getBorderWidth(style.rightBorderWidth)};"+
                "text-align: ${getHorizontalAlign(style)};"+
                "vertical-align:${getVerticalAlign(style)};"+
                "font-family: ${getFontFamily(style)};"+
                "font-style: ${getFontStyle(style)};"+
                "font-size: ${getFontSize(style)};"+
                "font-weight:${getFontWeight(style)};"+
                "text-decoration:${getFontDecoration(style)};"+
                 buildBackground(style)+
                "}\");")
        return builder.toString()
    }

    private fun buildBackground(style: GeneratedReportCellStyle): String{
        return when(style.foregroundColor){
            GeneratedReportColor.BLACK -> "background-color:black;"
            GeneratedReportColor.GREY_40_PERCENT -> "background-color: rgb(100, 100, 100);"
            GeneratedReportColor.GREY_25_PERCENT -> "background-color: rgb(180, 180, 180);"
            null -> ""
        }
    }

    private fun getFontDecoration(style: GeneratedReportCellStyle): String {
        return when(style.fontUnderline){
            GeneratedReportFontUnderline.SINGLE -> "underline"
            GeneratedReportFontUnderline.DOUBLE -> "underline"
            GeneratedReportFontUnderline.SINGLE_ACCOUNTING -> "underline"
            GeneratedReportFontUnderline.DOUBLE_ACCOUNTING -> "underline"
            GeneratedReportFontUnderline.NONE -> "none"
            null -> "none"
        }
    }

    private fun getFontWeight(style: GeneratedReportCellStyle): String {
        return if(style.fontBold == true) "bold" else "normal"
    }

    private fun getFontSize(style: GeneratedReportCellStyle): String {
        return "${style.fontHeight?:12}px"
    }

    private fun getFontStyle(style: GeneratedReportCellStyle): String {
        return if(style.fontItalic == true) "italic" else "normal"
    }

    private fun getFontFamily(style: GeneratedReportCellStyle): String {
        return style.fontFamily?:"sans-serif;"

    }

    private fun getVerticalAlign(style: GeneratedReportCellStyle): String {
        return when(style.verticalAlignment){
            GeneratedReportCellVerticalAlignment.TOP -> "top"
            GeneratedReportCellVerticalAlignment.CENTER -> "middle"
            GeneratedReportCellVerticalAlignment.BOTTOM -> "bottom"
            null -> "middle"
        }
    }

    private fun getHorizontalAlign(style: GeneratedReportCellStyle): String {
        return when(style.horizontalAlignment){
            GeneratedReportCellHorizontalAlignment.LEFT -> "left"
            GeneratedReportCellHorizontalAlignment.CENTER -> "center"
            GeneratedReportCellHorizontalAlignment.RIGHT -> "right"
            null -> "center"
        }
    }

    private fun getBorderWidth(topBorderWidth: GeneratedReportCellBorderWidth?): String {
        return when(topBorderWidth){
            GeneratedReportCellBorderWidth.NONE -> "0px"
            GeneratedReportCellBorderWidth.THIN -> "1px solid black"
            GeneratedReportCellBorderWidth.THICK -> "2px solid black"
            null -> "0px"
        }
    }

    private fun fillTable(zkTable: Table, list: GeneratedReportList, styles: ArrayList<GeneratedReportCellStyle>) {
        val colGroup = Colgroup()
        zkTable.appendChild(colGroup)
        list.columns.forEach {
            val col = Col()
            col.setClientAttribute("width", "${it.width*5}")
            colGroup.appendChild(col)
        }
        list.rows.withIndex().forEach {(ri, row) ->
            val zkTr = Tr()
            zkTable.appendChild(zkTr)
            row.height?.let { }
            zkTr.style = "height:${row.height?:20}px"
            row.cells.withIndex().forEach {(ci, cell) ->
                if(!isSkip(ri, ci, list.mergedRegions)){
                    val zkTd=Td()
                    cell.styleId?.let { zkTd.sclass = it }
                    getSpan(ri, ci, list.mergedRegions)?.let {
                        zkTd.rowspan = it.first
                        zkTd.colspan = it.second
                    }
                    zkTr.appendChild(zkTd)
                    val textValue = when(cell.contentType){
                        GeneratedReportCellValueType.NONE -> null
                        GeneratedReportCellValueType.NUMBER -> formatNumber(cell.styleId, styles, cell.value)
                        GeneratedReportCellValueType.DATE -> formatDate(cell.styleId, styles, cell.value)
                        GeneratedReportCellValueType.FORMULA -> formatNumber(cell.styleId, styles, cell.value)
                        GeneratedReportCellValueType.TEXT -> cell.value
                    }
                    if(!TextUtils.isBlank(textValue)){
                        zkTd.appendChild(Text(textValue))
                    }
                }
            }
        }
    }

    private fun formatDate(styleId: String?, styles: List<GeneratedReportCellStyle>, value: String?): String? {
        if(TextUtils.isBlank(value)){
            return null
        }
        val format = dateFormattersCache.getOrPut(styleId){
            styles.find { it.id == styleId }?.format?.let {  DateTimeFormatter.ofPattern(it)}?:DateTimeFormatter.ofPattern("yyyy.MM.dd")
        }
        return format.format(DATE_STORE_FORMATTER.parse(value))
    }

    private fun formatNumber(styleId: String?, styles: ArrayList<GeneratedReportCellStyle>, value: String?): String? {
        if(TextUtils.isBlank(value)){
            return null
        }
        val format = numberFormattersCache.getOrPut(styleId){
            styles.find { it.id == styleId }?.format?.let {  DecimalFormat(it)}?:DecimalFormat("#")
        }
        return format.format(value!!.toBigDecimal())
    }

    private fun isSkip(ri: Int, ci: Int, mergedRegions: List<GeneratedReportMergeRegion>): Boolean {
        return mergedRegions.any { !(it.leftTopRow == ri && it.leftTopColumn ==ci) && (ri >= it.leftTopRow && ri <= it.rightBottomRow)
                && (ci >= it.leftTopColumn && ci <= it.rightBottomColumn) }
    }

    private fun getSpan(ri: Int, ci: Int, mergedRegions: List<GeneratedReportMergeRegion>):Pair<Int,Int>?{
        return mergedRegions.find { (it.leftTopRow == ri && it.leftTopColumn ==ci) && (ri >= it.leftTopRow && ri <= it.rightBottomRow) }?.
        let{
            Pair(it.rightBottomRow-it.leftTopRow + 1, it.rightBottomColumn-it.leftTopColumn + 1)
        }
    }

    override fun getData(): GeneratedReport? {
        return reportData
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        component = Tabbox()
        component!!.orient ="bottom"
        configureBasicParameters(component!!, config)
        setDataInternal()
        return component!!
    }

}