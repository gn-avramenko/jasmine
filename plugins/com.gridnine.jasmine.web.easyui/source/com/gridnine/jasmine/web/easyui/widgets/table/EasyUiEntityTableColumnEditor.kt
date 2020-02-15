package com.gridnine.jasmine.web.easyui.widgets.table

import com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.EntitySelectDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.EntityTableColumnDescriptionJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiEntitySelectWidget

object EasyUiEntityTableColumnEditor {
    val init = {container:dynamic, options:EasyUiEntityTableColumnEditorConfiguration ->
        val uid = TextUtilsJS.createUUID()
        val descr = EntitySelectDescriptionJS("editor",options.description.entityClassName)
        val input = jQuery("<input id=\"editor$uid\">").asDynamic().appendTo(container)
        val widget = EasyUiEntitySelectWidget(uid, descr)
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
        val editor: EasyUiEntitySelectWidget = jQuery(target).data("jasmine-editor")
        editor.setData(value)
    }

    val getValue = {target:dynamic ->
        val editor: EasyUiEntitySelectWidget = jQuery(target).data("jasmine-editor")
        editor.getData()
    }

    val resize = {target:dynamic, width:dynamic ->
        jQuery(target).asDynamic()._outerWidth(width);
    }
}

class EasyUiEntityTableColumnEditorConfiguration(val description:EntityTableColumnDescriptionJS, val configuration: EntitySelectConfigurationJS)