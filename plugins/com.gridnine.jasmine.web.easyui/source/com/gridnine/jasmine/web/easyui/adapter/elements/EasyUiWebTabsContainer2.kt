/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.browser.window

class EasyUiWebTabsContainer2(private val parent:WebComponent?, configure: WebTabsContainerConfiguration.()->Unit) :WebTabsContainer{

    private var tbs:dynamic = null
    private var currentTb:dynamic = null
    private var idx = 1;

    override fun addTestTab() {
        idx++
        tbs!!.tabs("add",object{
            val title = "Tab " + idx
            val content = "<input id=\"tb"+idx+"\" type=\"text\" style=\"width:300px\">"
            val closable =true
        });
        currentTb = jQuery("#tb"+idx)
        currentTb.textbox()

    }

    override fun addTab(panel: WebTabPanel) {
        TODO("Not yet implemented")
    }

    override fun removeTab(id: String) {
        TODO("Not yet implemented")
    }

    override fun select(id: String) {
        TODO("Not yet implemented")
    }

    override fun getTabs(): List<WebTabPanel> {
        return emptyList()
    }

    override fun setTitle(tabId: String, title: String) {
        TODO("Not yet implemented")
    }

    override fun getParent(): WebComponent? {
        return null
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf()
    }

    override fun getHtml(): String {
        return "<div id=\"tt\" class=\"easyui-tabs\" style=\"width:100%;height:100%;\"></div>"
    }

    override fun decorate() {
        tbs = jQuery("#tt")
        tbs.tabs(object{
            val onClose = {
                if(currentTb != null){
                    // currentTb.textbox('destroy')
                    currentTb = null
                }
            }
        }
        )
    }

    override fun destroy() {
        //noops
    }


}