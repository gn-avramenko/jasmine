package com.gridnine.jasmine.web.easyui.widgets.table

import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.EnumSelectDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.EnumTableColumnDescriptionJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiEnumSelectWidget

object EasyUiEnumTableColumnEditor {
    val init = {container:dynamic, options:EasyUiEnumTableColumnEditorConfiguration<FakeEnumJS> ->
        val uid = TextUtilsJS.createUUID()
        val descr = EnumSelectDescriptionJS("editor",options.description.enumId)
        val input = jQuery("<input id=\"editor$uid\">").asDynamic().appendTo(container)
        val widget = EasyUiEnumSelectWidget<FakeEnumJS>(uid, descr)
        widget.configure(options.configuration)
        input.data("jasmine-editor", widget)
        input
    }

    val destroy = {target:dynamic ->
        val res = jQuery(target)
        res.asDynamic().data("jasmine-editor", null)
        res.remove()
    }
    val setValue = {target:dynamic, value:dynamic ->
        val editor: EasyUiEnumSelectWidget<FakeEnumJS> = jQuery(target).data("jasmine-editor")
        editor.setData(value)
    }

    val getValue = {target:dynamic ->
        val editor: EasyUiEnumSelectWidget<FakeEnumJS> = jQuery(target).data("jasmine-editor")
        editor.getData()
    }

    val resize = {target:dynamic, width:dynamic ->
        jQuery(target).asDynamic()._outerWidth(width);
    }
}

class EasyUiEnumTableColumnEditorConfiguration<E:Enum<E>>(val description:EnumTableColumnDescriptionJS, val configuration: EnumSelectConfigurationJS<E>)