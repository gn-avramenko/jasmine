///*****************************************************************
// * Gridnine AB http://www.gridnine.com
// * Project: Jasmine
// *****************************************************************/
//
//package com.gridnine.jasmine.web.easyui.adapter.elements
//
//import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS
//import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
//import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
//import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
//import com.gridnine.jasmine.web.core.ui.DefaultUIParameters
//import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
//import com.gridnine.jasmine.web.core.ui.WebComponent
//import com.gridnine.jasmine.web.core.ui.WebEditor
//import com.gridnine.jasmine.web.core.ui.components.*
//import com.gridnine.jasmine.web.core.ui.widgets.GridCellWidget
//import com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget
//import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
//import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
//import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
//import com.gridnine.jasmine.web.easyui.adapter.jQuery
//import kotlin.browser.window
//
//class EasyUiWebTabsContainer3(private val parent:WebComponent?, configure: WebTabsContainerConfiguration.()->Unit) :WebTabsContainer{
//
//    private var tbs:dynamic = null
//    private var idx = 1;
//
//    override fun addTestTab() {
//        val editor = DemoUserAccountWebEditor(this)
//        idx++
//        tbs!!.tabs("add",object{
//            val title = "Tab " + idx
//            val content = editor.getHtml()
//            val closable =true
//        });
//        editor.decorate()
//    }
//
//    override fun addTab(panel: WebTabPanel) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeTab(id: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun select(id: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getTabs(): List<WebTabPanel> {
//        return emptyList()
//    }
//
//    override fun getParent(): WebComponent? {
//        return null
//    }
//
//    override fun getChildren(): List<WebComponent> {
//        return arrayListOf()
//    }
//
//    override fun getHtml(): String {
//        return "<div id=\"tt\" class=\"easyui-tabs\" style=\"width:100%;height:100%;\"></div>"
//    }
//
//    override fun decorate() {
//        tbs = jQuery("#tt")
//        tbs.tabs(object{
//            val onClose = {
//            }
//        }
//        )
//    }
//
//    override fun destroy() {
//        //noops
//    }
//
//
//}
//
//class DemoUserAccountWebEditor(private val parent: WebComponent, private val delegate: WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(parent) {
////    height = "100%"
//})
//    : WebComponent by delegate, WebEditor<BaseVMJS, BaseVSJS, BaseVVJS> {
//
//
//    val loginWidget: TextBoxWidget
//
//    val nameWidget: TextBoxWidget
//
//    init {
//        delegate.defineColumn(DefaultUIParameters.controlWidthAsString)
//        delegate.addRow()
//        val loginCell = GridCellWidget(L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.demo.DemoUserAccountEditor"]?.get("login")
//                ?: "???",
//                delegate) { par ->
//            TextBoxWidget(par, {
//                width = "100%"
//            })
//        }
//        delegate.addCell(WebGridLayoutCell(loginCell))
//        loginWidget = loginCell.widget
//        delegate.addRow()
//        val nameCell = GridCellWidget(L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.demo.DemoUserAccountEditor"]?.get("name")
//                ?: "???",
//                delegate) { par ->
//            TextBoxWidget(par, {
//                width = "100%"
//            })
//        }
//        delegate.addCell(WebGridLayoutCell(nameCell))
//        nameWidget = nameCell.widget
//    }
//
//    override fun readData(vm: BaseVMJS, vs: BaseVSJS) {
//        //noops
//    }
//
//    override fun setReadonly(value: Boolean) {
//        //noops
//    }
//
//
//}