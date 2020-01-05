/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File


object UiWebGenerator {

    private fun  toGenData(descr: VMEntityDescription): GenClassData {

        val result = GenClassData(descr.id+"JS","com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS", abstract = false, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType),  getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }


    private fun getClassName(vmPropertyType: VMPropertyType, className: String?): String? {
        if(vmPropertyType == VMPropertyType.ENTITY_REFERENCE){
            return "com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS"
        }
        if(vmPropertyType == VMPropertyType.LOCAL_DATE){
            return "kotlin.js.Date"
        }
        if(vmPropertyType == VMPropertyType.LOCAL_DATE_TIME){
            return "kotlin.js.Date"
        }
        return className+"JS"
    }

    private fun getPropertyType(type: VMCollectionType): GenPropertyType {
        return when (type) {
            VMCollectionType.ENTITY ->GenPropertyType.ENTITY
        }
    }

    private fun getClassName(vmPropertyType: VMCollectionType, className: String?): String? {
        return when(vmPropertyType){
            VMCollectionType.ENTITY ->className+"JS"
        }
    }

    private fun getPropertyType(type: VMPropertyType): GenPropertyType {
        return when (type) {
            VMPropertyType.STRING ->GenPropertyType.STRING
            VMPropertyType.BIG_DECIMAL -> GenPropertyType.DOUBLE
            VMPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            VMPropertyType.ENTITY -> GenPropertyType.ENTITY
            VMPropertyType.SELECT -> GenPropertyType.ENTITY
            VMPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            VMPropertyType.ENUM -> GenPropertyType.ENUM
            VMPropertyType.INT -> GenPropertyType.INT
            VMPropertyType.LOCAL_DATE -> GenPropertyType.ENTITY
            VMPropertyType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            VMPropertyType.LONG -> GenPropertyType.LONG
        }
    }

    private fun  toGenData(descr: VSEntityDescription): GenClassData {

        val result = GenClassData(descr.id+"JS","com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS", abstract = false, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType),  getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(elementType: VSCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VSCollectionType.ENTITY -> elementClassName
        }
    }

    private fun getPropertyType(type: VSCollectionType): GenPropertyType {
        return when (type) {
            VSCollectionType.ENTITY ->GenPropertyType.ENTITY
        }
    }

    private fun getPropertyType(type: VSPropertyType): GenPropertyType {
        return when (type) {
            VSPropertyType.ENUM_SELECT ->GenPropertyType.ENTITY
            VSPropertyType.ENTITY_AUTOCOMPLETE ->GenPropertyType.ENTITY
            VSPropertyType.ENTITY ->GenPropertyType.ENTITY
            VSPropertyType.SELECT ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_ENTITY ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_ENUM_SELECT ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_TEXT ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_FLOAT ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_INT ->GenPropertyType.ENTITY
            VSPropertyType.COLUMN_DATE ->GenPropertyType.ENTITY
        }
    }
    private fun getClassName(propertyType: VSPropertyType, className: String?): String? {
        return when(propertyType){
            VSPropertyType.ENUM_SELECT -> "com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS<${className}JS>"
            VSPropertyType.ENTITY_AUTOCOMPLETE -> "com.gridnine.jasmine.web.core.model.ui.EntityAutocompleteConfigurationJS"
            VSPropertyType.ENTITY -> {
                if(className != null && className.startsWith(TableConfiguration::class.qualifiedName!!)){
                    val idx1 =className.indexOf("<")
                    val idx2 =className.length-1
                    "com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS<${className.substring(idx1+1,idx2)}JS>"
                } else {
                    throw IllegalArgumentException("unsupported classname $className")
                }
            }
            VSPropertyType.COLUMN_DATE -> "com.gridnine.jasmine.web.core.model.ui.DateColumnConfigurationJS"
            VSPropertyType.COLUMN_ENTITY -> "com.gridnine.jasmine.web.core.model.ui.EntityColumnConfigurationJS"
            VSPropertyType.COLUMN_ENUM_SELECT ->"com.gridnine.jasmine.web.core.model.ui.EnumColumnConfigurationJS<${className}JS>"
            VSPropertyType.COLUMN_INT -> "com.gridnine.jasmine.web.core.model.ui.IntegerColumnConfigurationJS"
            VSPropertyType.COLUMN_FLOAT-> "com.gridnine.jasmine.web.core.model.ui.FloatColumnConfigurationJS"
            VSPropertyType.COLUMN_TEXT -> "com.gridnine.jasmine.web.core.model.ui.TextColumnConfigurationJS"
            VSPropertyType.SELECT -> "com.gridnine.jasmine.web.core.model.ui.SelectItemJS"
        }
    }

    private fun  toGenData(descr: VVEntityDescription): GenClassData {

        val result = GenClassData(descr.id+"JS","com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS", abstract = false, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType),  getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getPropertyType(type: VVPropertyType): GenPropertyType {
        return when (type) {
            VVPropertyType.STRING ->GenPropertyType.STRING
        }
    }
    private fun getPropertyType(type: VVCollectionType): GenPropertyType {
        return when (type) {
            VVCollectionType.ENTITY ->GenPropertyType.ENTITY
        }
    }
    private fun getClassName(propertyType: VVPropertyType): String? {
        return when (propertyType) {
            VVPropertyType.STRING -> null
        }
    }

    private fun getClassName(propertyType: VVCollectionType, className: String?): String? {
        return when (propertyType) {
            VVCollectionType.ENTITY -> "${className}JS"
        }
    }

    private fun  toGenData(viewDescription: StandardViewDescription): GenClassData {
        val result = GenClassData(viewDescription.id,"com.gridnine.jasmine.web.core.model.ui.BaseView<${viewDescription.viewModel}JS,${viewDescription.viewSettings}JS,${viewDescription.viewValidation}JS>", abstract = false, enum = false, noEnumProperties = true)

        viewDescription.layout.widgets.values.forEach { prop ->
            when (prop){
                is  TableNextRowDescription,is TableNextColumnDescription,is LabelDescription ->{}
                else -> result.properties.add(GenPropertyDescription(prop.id, GenPropertyType.ENTITY, getClassName(prop), true))
            }
        }
        return result
    }

    private fun  toGenData(dialogDescription: DialogDescription, viewDescription: BaseViewDescription): GenClassData {

        return GenClassData(dialogDescription.id,"com.gridnine.jasmine.web.core.ui.Dialog<${viewDescription.viewModel}JS,${viewDescription.viewSettings}JS,${viewDescription.viewValidation}JS,${viewDescription.id}>", abstract = false, enum = false, noEnumProperties = true)
    }


    private fun getClassName(widgetDescription: BaseWidgetDescription): String? {
        return when(widgetDescription){
            is TableNextRowDescription ->null
            is TableNextColumnDescription ->null
            is EnumSelectDescription -> "com.gridnine.jasmine.web.core.model.ui.EnumSelectWidget<${widgetDescription.enumId}JS>"
            is EntityAutocompleteDescription ->"com.gridnine.jasmine.web.core.model.ui.EntityAutocompletetWidget"
            is TextboxDescription -> "com.gridnine.jasmine.web.core.model.ui.TextBoxWidget"
            is TextAreaDescription -> "com.gridnine.jasmine.web.core.model.ui.TextAreaWidget"
            is IntegerBoxDescription -> "com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget"
            is FloatBoxDescription ->"com.gridnine.jasmine.web.core.model.ui.FloatBoxWidget"
            is TableDescription ->"com.gridnine.jasmine.web.core.model.ui.TableWidget<${widgetDescription.className}VMJS,${widgetDescription.className}VSJS,${widgetDescription.className}VVJS>"
            is BooleanBoxDescription -> "com.gridnine.jasmine.web.core.model.ui.BooleanBoxWidget"
            is LabelDescription -> null
            is DateboxDescription ->"com.gridnine.jasmine.web.core.model.ui.DateBoxWidget"
            is DateTimeBoxDescription ->"com.gridnine.jasmine.web.core.model.ui.DateTimeBoxWidget"
            is PasswordBoxDescription ->"com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget"
            else -> throw IllegalArgumentException("unsupported widget $widgetDescription")
        }
    }

    fun generateUiClasses(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            domainData.getOrPut(it.getParameters("dest-client-plugin-id").first(), { arrayListOf() })
                    .add(File(projectDir, it.getParameters("file").first()))
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<GenClassData>()
            val classes = arrayListOf<String>()
            registry.viewModels.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
                classes.add(it.id + "JS")
            }
            registry.viewSettings.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
                classes.add(it.id + "JS")
            }
            registry.viewValidations.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
                classes.add(it.id + "JS")
            }
            registry.views.values.forEach {
                if(it is StandardViewDescription){
                    val docClassData = toGenData(it)
                    classesData.add(docClassData)
                    classes.add(it.id)
                }
            }
            registry.dialogs.values.forEach {dialogDescription ->
               dialogDescription.buttons.forEach { if(it.handler  != "stub") classes.add(it.handler) }
               val viewDescription = registry.views[dialogDescription.viewId]?:throw IllegalArgumentException("unable to find view for dialog ${dialogDescription.id}")
               classesData.add(toGenData(dialogDescription,viewDescription))
                classes.add(dialogDescription.id)
            }
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.UiReflectionUtilsJS", projectName, false)

            GenUtils.classBuilder(sb, "object UiReflectionUtilsJS") {
                blankLine()
                "fun registerWebUiClasses()"{
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerClass(\"$it\") {$it()}"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                }
            }
            val file = File(projectDir, "plugins/$key/source-gen/${key.replace(".", "/")}/UiReflectionUtilsJS.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
        }
    }
}
