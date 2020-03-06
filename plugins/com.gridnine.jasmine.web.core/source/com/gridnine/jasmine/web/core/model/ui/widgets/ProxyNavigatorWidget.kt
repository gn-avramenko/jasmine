/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.ui.*

class ProxyNavigatorWidget<VM:BaseVMEntityJS, VS:BaseVSEntityJS,VV:BaseVVEntityJS> : NavigatorWidget<VM,VS,VV>() {

    val model = arrayListOf<VM>()
    val settings = arrayListOf<VS>()
    val validation = arrayListOf<VV>()

    init {

        configure = {
            settings.clear()
            settings.addAll(it)
        }
        showValidation = {
            validation.clear()
            validation.addAll(it)
        }
        readData ={
            model.clear()
            model.addAll(it)
        }
        hasNavigationKey = {uid:String ->
            model.find { it.uid == uid } != null
        }
        writeData ={
            it.clear()
            it.addAll(model)
        }
        add = { vm, vs,  idx ->
            val index = idx?:model.size
            model.add(index, vm)
            settings.add(index, vs)
        }
        remove = {
            val idx = model.map { it.uid }.indexOf(it)
            if(idx != -1){
                model.removeAt(idx)
                settings.removeAt(idx)
            }
        }

    }



}