/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper

enum class MiscFieldType {

    STRING,
    ENUM,
    ENTITY,
    LONG,
    CLASS,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY
}

abstract class BaseMiscElementDescription(id:String):BaseModelElementDescription(id)

class MiscPropertyDescription(id: String, val type: MiscFieldType, val lateinit:Boolean, val nonNullable:Boolean): BaseMiscElementDescription(id){
    var className: String? = null
}

class MiscCollectionDescription(id: String, val elementType: MiscFieldType): BaseMiscElementDescription(id){
    var elementClassName: String? = null
}


class MiscMapDescription(id: String, val keyClassType: MiscFieldType, val valueClassType: MiscFieldType): BaseMiscElementDescription(id){
    var keyClassName: String? = null
    var valueClassName: String? = null
}

class MiscEnumItemDescription(id:String) : BaseMiscElementDescription(id)

class MiscEnumDescription(id:String) : BaseMiscElementDescription(id){
    val items = linkedMapOf<String, MiscEnumItemDescription>()
}


class MiscEntityDescription(id:String) : BaseMiscElementDescription(id) {

    var isAbstract:Boolean = false

    var extendsId:String? = null

    val properties = linkedMapOf<String, MiscPropertyDescription>()

    val collections = linkedMapOf<String, MiscCollectionDescription>()

    val maps = linkedMapOf<String, MiscMapDescription>()
}


class MiscMetaRegistry: Disposable {
    val enums = linkedMapOf<String, MiscEnumDescription>()

    val entities = linkedMapOf<String, MiscEntityDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(MiscMetaRegistry::class)
        fun get() = wrapper.get()
    }
}

