/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.common.NotificationType

interface UiLibraryAdapter{
    fun redirect(relativeUrl:String)
    fun createBorderLayout(configure:BorderContainerConfiguration.()->Unit):BorderContainer
    fun createLabel(configure:LabelConfiguration.()->Unit):Label
    fun createAccordionContainer(configure:AccordionContainerConfiguration.()->Unit):AccordionContainer
    fun createTabboxContainer(configure:TabboxConfiguration.()->Unit):Tabbox
    fun createGridLayoutContainer(configure:GridLayoutContainerConfiguration.()->Unit):GridLayoutContainer
    fun<E:BaseIntrospectableObject> createDataGrid(configure:DataGridConfiguration.()->Unit):DataGrid<E>
    fun createTextBox(configure:TextBoxComponentConfiguration.()->Unit):TextBox
    fun createPasswordBox(configure:PasswordBoxComponentConfiguration.()->Unit):PasswordBox
    fun createLinkButton(configure:LinkButtonConfiguration.()->Unit):LinkButton
    fun createDateBox(configure:DateBoxComponentConfiguration.()->Unit):DateBox
    fun createDateTimeBox(configure:DateTimeBoxComponentConfiguration.()->Unit):DateTimeBox
    fun createNumberBox(configure:NumberBoxComponentConfiguration.()->Unit):NumberBox
    fun createSelect(configure:SelectConfiguration.()->Unit): Select
    fun<W> showDialog(dialogContent:W, configure:DialogConfiguration<W>.()->Unit):Dialog<W>  where W: UiNode
    fun createMenuButton(configure:MenuButtonConfiguration.()->Unit):MenuButton
    fun createPanel(configure:PanelConfiguration.()->Unit):Panel
    fun createTilesContainer(configure:TilesContainerConfiguration.()->Unit):TilesContainer
    fun createDivsContainer(configure:DivsContainerConfiguration.()->Unit):DivsContainer
    fun createBooleanBox(configure:BooleanBoxComponentConfiguration.()->Unit):BooleanBox
    fun createTable(configure:TableConfiguration.()->Unit):Table
    fun createTree(configure:TreeConfiguration.()->Unit):Tree
    fun showContextMenu(items:List<ContextMenuItem>, pageX:Int, pageY:Int)
    fun showNotification(message:String, type: NotificationType, timeout:Int)
    fun findRootComponent(): UiNode
    fun save(content:ByteArray, contentType:String, sugestedFileName:String?)

    companion object{
        private val wrapper = PublishableWrapper(UiLibraryAdapter::class)
        fun get() = wrapper.get()
    }
}