/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.SelectDataType
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebSelect

class GeneralSelectWidget(private val parent:WebComponent, configure:GeneralSelectWidgetConfiguration.()->Unit):WebComponent{
    private val delegate:WebSelect
    private val conf = GeneralSelectWidgetConfiguration();
    var changeListener:((SelectItemJS?) ->Unit)? = null
    set(value) = delegate.setChangeListener {values ->
        value?.let {
        it.invoke(if(values.isNotEmpty()) values[0] else null)
    } }
    init {
        conf.configure()
        delegate = UiLibraryAdapter.get().createSelect(this){
            width = conf.width
            height = conf.height
            mode = SelectDataType.LOCAL
            editable = false
            multiple = false
            showClearIcon = conf.showClearIcon
        }
    }

    fun setPossibleValues(values:List<SelectItemJS>){
        delegate.setPossibleValues(values)
    }

    fun getValue():SelectItemJS? {
        val values = delegate.getValues()
        return if(values.isEmpty()) null else values[0]
    }

    fun setValue(value: SelectItemJS?) {
        val values = arrayListOf<SelectItemJS>()
        if(value != null){
            values.add(value)
        }
        delegate.setValues(values)
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    fun setReadonly(value:Boolean) {
        delegate.setEnabled(!value)
    }

    fun configure(config: GeneralSelectBoxConfigurationJS?){
        config?.let {
            delegate.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        delegate.showValidation(value)
    }

    override fun destroy() {
        delegate.destroy()
    }
}

class GeneralSelectWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}