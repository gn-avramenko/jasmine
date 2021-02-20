/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiDivsContainer
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiGridLayoutContainer
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTextBox
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button

class DivsContainerPanel : ZkServerUiGridLayoutContainer(createConfiguration()){

    init {
        val divsContainerConfig = ServerUiDivsContainerConfiguration()
        divsContainerConfig.width = "100%"
        divsContainerConfig.height = "100%"
        val divsContainer = ZkServerUiDivsContainer(divsContainerConfig)

        val config1 = ServerUiTextBoxConfiguration()
        config1.width = "100%"
        config1.height = "20px"
        val tb1 = ZkServerUiTextBox(config1)
        tb1.setValue("textbox 1")

        val config2 = ServerUiTextBoxConfiguration()
        config2.width = "100%"
        config2.height = "20px"
        val tb2 = ZkServerUiTextBox(config2)
        tb2.setValue("textbox 2")
        addRow("auto")
        addCell(ServerUiGridLayoutCell(TestButton("Textbox1"){
            divsContainer.getDiv("tb1")?:run{
                divsContainer.addDiv("tb1", tb1)
            }
            divsContainer.show("tb1")
        }, 1))
        addCell(ServerUiGridLayoutCell(TestButton("Textbox2"){
            divsContainer.getDiv("tb2")?:run{
                divsContainer.addDiv("tb2", tb2)
            }
            divsContainer.show("tb2")
        }, 1))
        addRow("100%")
        addCell(ServerUiGridLayoutCell(divsContainer, 2))
    }

    companion object{
        private fun createConfiguration(): ServerUiGridLayoutContainerConfiguration {
            val result = ServerUiGridLayoutContainerConfiguration()
            result.height = "100%"
            result.width = "100%"
            result.noPadding = false
            result.columns.add(ServerUiGridLayoutColumnConfiguration("200px"))
            result.columns.add(ServerUiGridLayoutColumnConfiguration("200px"))
            return result
        }
    }
}

class TestButton(val  text:String, handler: ()->Unit ): ZkServerUiComponent(){

    val comp = Button()

    init {
        comp.label = text
        comp.addEventListener(Events.ON_CLICK){
            handler.invoke()
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        return comp
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}