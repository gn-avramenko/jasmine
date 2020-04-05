package com.gridnine.jasmine.web.core.test.ui

import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.model.ui.widgets.*
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import kotlin.js.Date

object TestViewBuilder {
    fun<VM: BaseVMEntityJS,VS: BaseVSEntityJS,VV: BaseVVEntityJS, V: BaseView<VM, VS, VV>> createView(viewId: String, uid:String):V {
        val view = ReflectionFactoryJS.get().getFactory(viewId).invoke() as V
        val viewDescr = UiMetaRegistryJS.get().views[viewId]
                ?: throw IllegalArgumentException("unable to load description for view $viewId")
        val widgetsDescriptions = arrayListOf<BaseWidgetDescriptionJS>()
        when (viewDescr) {
            is StandardViewDescriptionJS -> {
                when (val layout = viewDescr.layout) {
                    is TableLayoutDescriptionJS -> layout.widgets.values.forEach { widgetsDescriptions.add(it) }
                    else -> throw IllegalArgumentException("unsupported layout type  ${layout::class.simpleName}")
                }
            }
            else -> throw IllegalArgumentException("unsupported view type  ${viewDescr::class.simpleName}")
        }
        val wdsIds = arrayListOf<String>()
        widgetsDescriptions.forEach {wd ->
            when(wd){
                is TextboxDescriptionJS -> {
                    view.setValue(wd.id, ProxyTextBoxWidget())
                    wdsIds.add(wd.id)
                }
                is PasswordBoxDescriptionJS -> {
                    view.setValue(wd.id, ProxyPasswordBoxWidget())
                    wdsIds.add(wd.id)
                }
                is TileDescriptionJS -> {
                    view.setValue(wd.id, ProxyTileWidget<BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(uid, wd))
                    wdsIds.add(wd.id)
                }
                is DateboxDescriptionJS -> {
                    view.setValue(wd.id, ProxyDateBoxWidget())
                    wdsIds.add(wd.id)
                }
                is DateTimeBoxDescriptionJS -> {
                    view.setValue(wd.id, ProxyDateTimeBoxWidget())
                    wdsIds.add(wd.id)
                }
                is EnumSelectDescriptionJS -> {
                    view.setValue(wd.id, ProxyEnumSelectWidget<FakeEnumJS>())
                    wdsIds.add(wd.id)
                }
                is EntitySelectDescriptionJS ->{
                    view.setValue(wd.id, ProxyEntitySelectWidget())
                    wdsIds.add(wd.id)
                }
                is FloatBoxDescriptionJS -> {
                    view.setValue(wd.id, ProxyFloatBoxWidget())
                    wdsIds.add(wd.id)
                }
                is IntegerBoxDescriptionJS ->{
                    view.setValue(wd.id, ProxyIntBoxWidget())
                    wdsIds.add(wd.id)
                }
                is SelectDescriptionJS -> {
                    view.setValue(wd.id, ProxySelectWidget())
                    wdsIds.add(wd.id)
                }
                is BooleanBoxDescriptionJS -> {
                    view.setValue(wd.id,ProxyBooleanBoxWidget())
                    wdsIds.add(wd.id)
                }
                is TableDescriptionJS -> {
                    view.setValue(wd.id, ProxyTableWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>())
                    wdsIds.add(wd.id)
                }
                is NavigatorDescriptionJS -> {
                    view.setValue(wd.id, ProxyNavigatorWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>())
                    wdsIds.add(wd.id)
                }
                else -> {}

            }
        }

        wdsIds.forEach {id ->
            val widget = view.getValue(id)
            if(widget is WidgetWithParent){
                widget.parent = view
            }
        }

        var modelUid :String? = null
        view.readData = {model ->
            modelUid = model.uid
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).setData(model.getValue(it.id) as String?)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).setData(model.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).setData(model.getValue(it.id) as Double?)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).setData(model.getValue(it.id) as Int?)
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).setData(model.getValue(it.id) as Date?)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).setData(model.getValue(it.id) as Date?)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).setData(model.getValue(it.id) as Boolean?)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>).readData(model.getCollection(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*, *>).setData(model.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*, *, *>).readData(model.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.configure = {settings ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).configure(Unit)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).configure(Unit)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).configure(Unit)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).configure(Unit)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).configure(settings.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).configure(settings.getValue(it.id).asDynamic())
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).configure(Unit)
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).configure(Unit)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).configure(Unit)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).configure(Unit)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*, *>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*, *, *>).configure(settings.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.showValidation = {validation ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).showValidation(validation.getValue(it.id) as String?)
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).showValidation(validation.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>).showValidation(validation.getValue(it.id)?.asDynamic())
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*, *>).showValidation(validation.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*, *, *>).showValidation(validation.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.writeData = {model ->
            model.uid = modelUid
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TextBoxWidget).getData())
                    }
                    is BooleanBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as BooleanBoxWidget).getData())
                    }
                    is PasswordBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as PasswordBoxWidget).getData())
                    }
                    is TextAreaDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TextAreaWidget).getData())
                    }
                    is EnumSelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as EnumSelectWidget<*>).getData())
                    }
                    is SelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as SelectWidget).getData())
                    }
                    is EntitySelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as EntitySelectWidget).getData())
                    }
                    is FloatBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as FloatBoxWidget).getData())
                    }
                    is IntegerBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as IntegerBoxWidget).getData())
                    }
                    is DateboxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as DateBoxWidget).getData())
                    }
                    is DateTimeBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as DateTimeBoxWidget).getData())
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>).writeData(model.getCollection(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TileWidget<*, *>).getData())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*, *, *>).writeData(model.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.navigate = navigate@{uid  ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TileDescriptionJS -> {
                        val result = (view.getValue(it.id) as TileWidget<*, *>).navigate(uid)
                        if(result){
                            return@navigate true
                        }
                    }
                    is NavigatorDescriptionJS -> {
                        val result = (view.getValue(it.id) as NavigatorWidget<*, *, *>).navigate(uid)
                        if(result){
                            return@navigate true
                        }
                    }
                }
            }
            false
        }
        val interceptors = arrayListOf<ViewInterceptor<VM, VS, VV, V>>()
        viewDescr.interceptors.forEach {
            interceptors.add(ReflectionFactoryJS.get().getFactory(it).invoke() as ViewInterceptor<VM, VS, VV, V>) }
        interceptors.forEach {
            it.onCreate(view)
        }
        return view
    }
}