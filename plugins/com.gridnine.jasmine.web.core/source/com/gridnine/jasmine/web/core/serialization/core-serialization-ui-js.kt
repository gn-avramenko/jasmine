/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*

internal object UiSerializationUtilsJS {


    val uiProviderFactory = object : ProviderFactoryJS {
        private fun createTableConfigurationDescription():ObjectMetadataProviderJS<TableConfigurationJS<BaseVSEntityJS>>{
            return object:ObjectMetadataProviderJS<TableConfigurationJS<BaseVSEntityJS>>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(TableConfigurationJS.columnSettings, SerializablePropertyTypeJS.ENTITY,BaseVSEntityJS.qualifiedClassName , true))
                }
                override fun getPropertyValue(obj: TableConfigurationJS<BaseVSEntityJS>, id: String): Any? {
                    if(id == TableConfigurationJS.columnSettings){
                        return obj.columnSettings
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: TableConfigurationJS<BaseVSEntityJS>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: TableConfigurationJS<BaseVSEntityJS>, id: String, value: Any?) {
                    if(id == TableConfigurationJS.columnSettings){
                        obj.columnSettings = value as BaseVSEntityJS
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

            }
        }
        private fun createTextColumnConfigurationDescription():ObjectMetadataProviderJS<TextColumnConfigurationJS>{
            return object:ObjectMetadataProviderJS<TextColumnConfigurationJS>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(BaseColumnConfigurationJS.notEditable, SerializablePropertyTypeJS.BOOLEAN, null, false))
                }
                override fun getPropertyValue(obj: TextColumnConfigurationJS, id: String): Any? {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        return obj.notEditable
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: TextColumnConfigurationJS, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: TextColumnConfigurationJS, id: String, value: Any?) {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        obj.notEditable = value as Boolean
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

            }
        }

        private fun createIntColumnConfigurationDescription():ObjectMetadataProviderJS<IntegerColumnConfigurationJS>{
            return object:ObjectMetadataProviderJS<IntegerColumnConfigurationJS>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(BaseColumnConfigurationJS.notEditable, SerializablePropertyTypeJS.BOOLEAN, null, false))
                }
                override fun getPropertyValue(obj: IntegerColumnConfigurationJS, id: String): Any? {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        return obj.notEditable
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: IntegerColumnConfigurationJS, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: IntegerColumnConfigurationJS, id: String, value: Any?) {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        obj.notEditable = value as Boolean
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

            }

        }
        private fun createFloatColumnConfigurationDescription():ObjectMetadataProviderJS<FloatColumnConfigurationJS>{
            return object:ObjectMetadataProviderJS<FloatColumnConfigurationJS>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(BaseColumnConfigurationJS.notEditable, SerializablePropertyTypeJS.BOOLEAN, null, false))
                }
                override fun getPropertyValue(obj: FloatColumnConfigurationJS, id: String): Any? {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        return obj.notEditable
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: FloatColumnConfigurationJS, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: FloatColumnConfigurationJS, id: String, value: Any?) {
                    if("notEditable" == id){
                        obj.notEditable = value as Boolean
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

            }

        }
        private fun createEnumColumnConfigurationDescription():ObjectMetadataProviderJS<EnumColumnConfigurationJS<*>>{
            return object:ObjectMetadataProviderJS<EnumColumnConfigurationJS<*>>(){
                init{
                    properties.add(SerializablePropertyDescriptionJS(BaseColumnConfigurationJS.notEditable, SerializablePropertyTypeJS.BOOLEAN, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EnumColumnConfigurationJS.nullAllowed, SerializablePropertyTypeJS.BOOLEAN, null, false))
                }
                override fun getPropertyValue(obj: EnumColumnConfigurationJS<*>, id: String): Any? {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        return obj.notEditable
                    }
                    if(EnumColumnConfigurationJS.nullAllowed == id){
                        return obj.nullAllowed
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getCollection(obj: EnumColumnConfigurationJS<*>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("no collection with id $id")
                }

                override fun setPropertyValue(obj: EnumColumnConfigurationJS<*>, id: String, value: Any?) {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        obj.notEditable = value as Boolean
                        return
                    }
                    if(EnumColumnConfigurationJS.nullAllowed == id){
                        obj.nullAllowed = value as Boolean
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun hasUid(): Boolean {
                    return false
                }

            }
        }
        private fun createEntityColumnConfigurationDescription():ObjectMetadataProviderJS<EntityColumnConfigurationJS>{
            return object : ObjectMetadataProviderJS<EntityColumnConfigurationJS>() {
                init{
                    properties.add(SerializablePropertyDescriptionJS(BaseColumnConfigurationJS.notEditable, SerializablePropertyTypeJS.BOOLEAN, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityColumnConfigurationJS.limit, SerializablePropertyTypeJS.INT, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityColumnConfigurationJS.nullAllowed, SerializablePropertyTypeJS.BOOLEAN, null, false))
                    collections.add(SerializableCollectionDescriptionJS(EntityColumnConfigurationJS.dataSources, SerializablePropertyTypeJS.ENTITY, "com.flinty.jasmine.web.model.ui.EntityAutocompleteDataSourceJS", false))
                }

                override fun hasUid(): Boolean {
                    return false
                }

                override fun getCollection(obj: EntityColumnConfigurationJS, id: String): MutableCollection<Any> {
                    if(EntityColumnConfigurationJS.dataSources == id){
                        return obj.dataSources as MutableCollection<Any>
                    }
                    throw IllegalArgumentException("no collection fields")
                }

                override fun setPropertyValue(obj: EntityColumnConfigurationJS, id: String, value: Any?) {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        obj.notEditable = value as Boolean
                        return
                    }
                    if(EntityColumnConfigurationJS.nullAllowed == id){
                        obj.nullAllowed = value as Boolean?
                        return
                    }
                    if(EntityColumnConfigurationJS.limit == id){
                        obj.limit = value as Int
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getPropertyValue(obj: EntityColumnConfigurationJS, id: String): Any? {
                    if(BaseColumnConfigurationJS.notEditable == id){
                        return obj.notEditable
                    }
                    if(EntityColumnConfigurationJS.nullAllowed == id){
                        return obj.nullAllowed
                    }
                    if(EntityColumnConfigurationJS.limit == id){
                        return obj.limit
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

            }
        }
        private fun createEnumSelectConfigurationDescription(): ObjectMetadataProviderJS<EnumSelectConfigurationJS<*>> {
            return object : ObjectMetadataProviderJS<EnumSelectConfigurationJS<*>>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(EnumSelectConfigurationJS.nullAllowed, SerializablePropertyTypeJS.BOOLEAN, null, false))
                }

                override fun getCollection(obj: EnumSelectConfigurationJS<*>, id: String): MutableCollection<Any> {
                    throw IllegalArgumentException("unsupported property $id")
                }

                override fun setPropertyValue(obj: EnumSelectConfigurationJS<*>, id: String, value: Any?) {
                    if (EnumSelectConfigurationJS.nullAllowed == id) {
                        obj.nullAllowed = value as Boolean
                        return
                    }
                    throw IllegalArgumentException("unsupported property $id")
                }

                override fun getPropertyValue(obj: EnumSelectConfigurationJS<*>, id: String): Any? {
                    if (EnumSelectConfigurationJS.nullAllowed == id) {
                        return obj.nullAllowed
                    }
                    throw IllegalArgumentException("unsupported property $id")
                }

            }
        }

        private fun createEntityAutocompleteDatasourceDescription(): ObjectMetadataProviderJS<EntityAutocompleteDataSourceJS> {
            return object : ObjectMetadataProviderJS<EntityAutocompleteDataSourceJS>() {
                init{
                    properties.add(SerializablePropertyDescriptionJS(EntityAutocompleteDataSourceJS.name, SerializablePropertyTypeJS.STRING, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityAutocompleteDataSourceJS.autocompleteId, SerializablePropertyTypeJS.STRING, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityAutocompleteDataSourceJS.indexClassName, SerializablePropertyTypeJS.STRING, null, false))
                    collections.add(SerializableCollectionDescriptionJS(EntityAutocompleteDataSourceJS.columnsNames, SerializablePropertyTypeJS.STRING, null, false))
                }

                override fun hasUid(): Boolean {
                    return false
                }

                override fun getCollection(obj: EntityAutocompleteDataSourceJS, id: String): MutableCollection<Any> {
                    if(EntityAutocompleteDataSourceJS.columnsNames == id){
                        return obj.columnsNames as MutableCollection<Any>
                    }
                    throw IllegalArgumentException("no collection fields")
                }

                override fun setPropertyValue(obj: EntityAutocompleteDataSourceJS, id: String, value: Any?) {
                    if(EntityAutocompleteDataSourceJS.name == id){
                        obj.name = value as String?
                        return
                    }
                    if(EntityAutocompleteDataSourceJS.autocompleteId == id){
                        obj.autocompleteId = value as String?
                        return
                    }
                    if(EntityAutocompleteDataSourceJS.indexClassName == id){
                        obj.indexClassName = value as String?
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getPropertyValue(obj: EntityAutocompleteDataSourceJS, id: String): Any? {
                    if(EntityAutocompleteDataSourceJS.name == id){
                        return obj.name
                    }
                    if(EntityAutocompleteDataSourceJS.autocompleteId == id){
                        return obj.autocompleteId
                    }
                    if(EntityAutocompleteDataSourceJS.indexClassName == id){
                        return obj.indexClassName
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

            }
        }

        private fun createEntityAutocompleteConfigurationDescription(): ObjectMetadataProviderJS<EntityAutocompleteConfigurationJS> {
            return object : ObjectMetadataProviderJS<EntityAutocompleteConfigurationJS>() {
                init {
                    properties.add(SerializablePropertyDescriptionJS(EntityAutocompleteConfigurationJS.limit, SerializablePropertyTypeJS.INT, null, false))
                    properties.add(SerializablePropertyDescriptionJS(EntityAutocompleteConfigurationJS.nullAllowed, SerializablePropertyTypeJS.BOOLEAN, null, false))
                    collections.add(SerializableCollectionDescriptionJS(EntityAutocompleteConfigurationJS.dataSources, SerializablePropertyTypeJS.ENTITY, "com.flinty.jasmine.web.model.ui.EntityAutocompleteDataSourceJS", false))
                }

                override fun hasUid(): Boolean {
                    return false
                }

                override fun getCollection(obj: EntityAutocompleteConfigurationJS, id: String): MutableCollection<Any> {
                    if (EntityAutocompleteConfigurationJS.dataSources == id) {
                        return obj.dataSources as MutableCollection<Any>
                    }
                    throw IllegalArgumentException("no collection fields")
                }

                override fun setPropertyValue(obj: EntityAutocompleteConfigurationJS, id: String, value: Any?) {
                    if (EntityAutocompleteConfigurationJS.nullAllowed == id) {
                        obj.nullAllowed = value as Boolean?
                        return
                    }
                    if (EntityAutocompleteConfigurationJS.limit == id) {
                        obj.limit = value as Int
                        return
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

                override fun getPropertyValue(obj: EntityAutocompleteConfigurationJS, id: String): Any? {
                    if (EntityAutocompleteConfigurationJS.nullAllowed == id) {
                        return obj.nullAllowed
                    }
                    if (EntityAutocompleteConfigurationJS.limit == id) {
                        return obj.limit
                    }
                    throw IllegalArgumentException("no field with id $id")
                }

            }
        }


        private fun createVMEntityDescription(descr: VMEntityDescriptionJS): ObjectMetadataProviderJS<BaseVMEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVMEntityJS>() {
                override fun hasUid(): Boolean {
                    return true
                }

                init {
                    properties.add(SerializablePropertyDescriptionJS(BaseEntityJS.uid, SerializablePropertyTypeJS.STRING, null, false))
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), it.elementClassName, isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVMEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVMEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVMEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createVSEntityDescription(descr: VSEntityDescriptionJS): ObjectMetadataProviderJS<BaseVSEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVSEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type, it.className), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVSEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVSEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVSEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun createVVEntityDescription(descr: VVEntityDescriptionJS): ObjectMetadataProviderJS<BaseVVEntityJS> {
            return object : ObjectMetadataProviderJS<BaseVVEntityJS>() {
                override fun hasUid(): Boolean {
                    return false
                }

                init {
                    descr.properties.values.forEach {
                        properties.add(SerializablePropertyDescriptionJS(it.id, toSerializableType(it.type), toClassName(it.type), false))
                    }
                    descr.collections.values.forEach {
                        collections.add(SerializableCollectionDescriptionJS(it.id, toSerializableType(it.elementType), toClassName(it.elementType, it.elementClassName), isAbstractClass(it.elementClassName)))
                    }

                }

                override fun getCollection(obj: BaseVVEntityJS, id: String): MutableCollection<Any> {
                    return obj.getCollection(id)
                }

                override fun setPropertyValue(obj: BaseVVEntityJS, id: String, value: Any?) {
                    obj.setValue(id, value)
                }

                override fun getPropertyValue(obj: BaseVVEntityJS, id: String): Any? {
                    return obj.getValue(id)
                }

            }
        }

        private fun isAbstractClass(elementClassName: String?): Boolean {
            if (elementClassName == null) {
                return false
            }
            if (elementClassName == "com.flinty.jasmine.core.ui.model.BaseVMEntity") {
                return true
            }
            if (elementClassName == "com.flinty.jasmine.core.ui.model.BaseVSEntity") {
                return true
            }
            if (elementClassName == "com.flinty.jasmine.core.ui.model.BaseVSEntity") {
                return true
            }
            return false

        }

        private fun toSerializableType(elementType: VMCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VMCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }
        

        private fun toSerializableType(elementType: VMPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VMPropertyTypeJS.LONG -> SerializablePropertyTypeJS.LONG
                VMPropertyTypeJS.LOCAL_DATE_TIME -> SerializablePropertyTypeJS.LOCAL_DATE_TIME
                VMPropertyTypeJS.LOCAL_DATE -> SerializablePropertyTypeJS.LOCAL_DATE
                VMPropertyTypeJS.INT -> SerializablePropertyTypeJS.INT
                VMPropertyTypeJS.ENUM -> SerializablePropertyTypeJS.ENUM
                VMPropertyTypeJS.ENTITY_REFERENCE -> SerializablePropertyTypeJS.ENTITY
                VMPropertyTypeJS.BOOLEAN -> SerializablePropertyTypeJS.BOOLEAN
                VMPropertyTypeJS.BIG_DECIMAL -> SerializablePropertyTypeJS.BIG_DECIMAL
                VMPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
                VMPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VMPropertyTypeJS, elementClassName: String?): String? {
            return when (elementType){
                VMPropertyTypeJS.ENTITY_REFERENCE ->EntityReferenceJS.qualifiedClassName
                else -> elementClassName
            }
        }

        private fun toSerializableType(elementType: VSCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VSCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }
        
        private fun toSerializableType(elementType: VSPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VSPropertyTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.ENUM_SELECT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.ENTITY_AUTOCOMPLETE -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_TEXT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_INT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_FLOAT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_ENUM_SELECT -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_ENTITY -> SerializablePropertyTypeJS.ENTITY
                VSPropertyTypeJS.COLUMN_DATE -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VSPropertyTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VSPropertyTypeJS.ENTITY -> elementClassName
                VSPropertyTypeJS.ENUM_SELECT -> EnumSelectConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.ENTITY_AUTOCOMPLETE -> EntityAutocompleteConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_TEXT -> TextColumnConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_INT -> IntegerColumnConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_FLOAT -> FloatColumnConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_ENUM_SELECT ->EnumColumnConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_ENTITY -> EntityColumnConfigurationJS.qualifiedClassName
                VSPropertyTypeJS.COLUMN_DATE -> DateColumnConfigurationJS.qualifiedClassName
            }
        }

        private fun toClassName(elementType: VSCollectionTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VSCollectionTypeJS.ENTITY -> elementClassName
            }
        }

        private fun toSerializableType(elementType: VVPropertyTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VVPropertyTypeJS.STRING -> SerializablePropertyTypeJS.STRING
            }
        }

        private fun toClassName(elementType: VVPropertyTypeJS): String? {
            return when (elementType) {
                VVPropertyTypeJS.STRING -> null
            }
        }

        private fun toSerializableType(elementType: VVCollectionTypeJS): SerializablePropertyTypeJS {
            return when (elementType) {
                VVCollectionTypeJS.ENTITY -> SerializablePropertyTypeJS.ENTITY
            }
        }

        private fun toClassName(elementType: VVCollectionTypeJS, elementClassName: String?): String? {
            return when (elementType) {
                VVCollectionTypeJS.ENTITY -> elementClassName
            }
        }

        override fun create(className: String): ObjectMetadataProviderJS<out Any> {
            if (className == EnumSelectConfigurationJS.qualifiedClassName) {
                return createEnumSelectConfigurationDescription()
            }
            if (className == EntityAutocompleteDataSourceJS.qualifiedClassName) {
                return createEntityAutocompleteDatasourceDescription()
            }
            if (className == EntityAutocompleteConfigurationJS.qualifiedClassName) {
                return createEntityAutocompleteConfigurationDescription()
            }

            if(className == TableConfigurationJS.qualifiedClassName){
                return createTableConfigurationDescription()
            }
            if(className == TextColumnConfigurationJS.qualifiedClassName){
                return createTextColumnConfigurationDescription()
            }
            if(className == IntegerColumnConfigurationJS.qualifiedClassName){
                return createIntColumnConfigurationDescription()
            }
            if(className == FloatColumnConfigurationJS.qualifiedClassName){
                return createFloatColumnConfigurationDescription()
            }
            if(className == EnumColumnConfigurationJS.qualifiedClassName){
                return createEnumColumnConfigurationDescription()
            }
            if(className == EntityColumnConfigurationJS.qualifiedClassName){
                return createEntityColumnConfigurationDescription()
            }
            val vmDescription = UiMetaRegistryJS.get().viewModels[className]
            if (vmDescription != null) {
                return createVMEntityDescription(vmDescription)
            }
            val vsDescription = UiMetaRegistryJS.get().viewSettings[className]
            if (vsDescription != null) {
                return createVSEntityDescription(vsDescription)
            }
            val vvDescription = UiMetaRegistryJS.get().viewValidations[className]
            if (vvDescription != null) {
                return createVVEntityDescription(vvDescription)
            }
            throw RuntimeException("unsupported type $className")
        }
    }

}