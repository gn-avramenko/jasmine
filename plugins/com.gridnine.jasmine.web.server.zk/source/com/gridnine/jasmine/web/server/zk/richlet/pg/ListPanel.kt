/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zul.*
import org.zkoss.zul.impl.LabelImageElement

class ListPanel : Vlayout(){
    init{
        val l1 = Hlayout()
        l1.hflex = "1"
        val button = Button()
        button.label = "Load"
        l1.appendChild(button)
        val hGlue = Div()
        hGlue.hflex = "1"
        l1.appendChild(hGlue)
        val searchBox = Textbox()
        searchBox.width = "200px"
        l1.appendChild(searchBox)
        appendChild(l1)
        val grid = Grid()
        grid.vflex ="1"
        grid.hflex = "1"

        appendChild(grid)
    }
}