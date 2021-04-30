/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.CustomMetaRegistry
import com.gridnine.jasmine.common.core.meta.CustomType
import com.gridnine.jasmine.common.core.meta.WebMessagesMetaRegistry
import com.gridnine.jasmine.common.core.parser.CustomMetadataParser
import com.gridnine.jasmine.common.core.parser.WebMessagesMetadataParser
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class MetadataRestHandler:RestHandler<GetMetadataRequest, GetMetadataResponse>{
    private val coreResponse = lazy {
        val result = GetMetadataResponse()
        val customMetaregistry = CustomMetaRegistry()
        CustomMetadataParser.updateCustomMetaRegistry(customMetaregistry, "com/gridnine/jasmine/common/core/meta/core-custom.xml", javaClass.classLoader)
        updateCustomMetadata(result, customMetaregistry, null)
        val webMessagesRegistry = WebMessagesMetaRegistry()
        WebMessagesMetadataParser.updateWebMessages(webMessagesRegistry, "com/gridnine/jasmine/common/core/meta/core-web-messages.xml", javaClass.classLoader)
        updateWebMessagesMetadata(result, webMessagesRegistry, null)
        result
    }

    private fun updateWebMessagesMetadata(result: GetMetadataResponse, webMessagesRegistry: WebMessagesMetaRegistry, moduleId: String?) {
        webMessagesRegistry.bundles.values.forEach{bnd ->
            val mb = WebMessagesBundleDT()
            mb.id = bnd.id
            result.webMessages.add(mb)
            bnd.messages.values.forEach {msg ->
                val m = WebMessageDT()
                m.id = msg.id
                m.displayName = msg.getDisplayName()
                mb.messages.add(m)
            }
        }
    }

    private fun updateCustomMetadata(result: GetMetadataResponse, registry: CustomMetaRegistry, moduleId: String?) {
        registry.enums.values.forEach {en->
            val ed = CustomEnumDescriptionDT()
            ed.id = en.id
            result.customEnums.add(ed)
            en.items.values.forEach {
                ed.items.add(it.id)
            }
        }
        registry.entities.values.forEach {ett->
            val ed = CustomEntityDescriptionDT()
            ed.id = ett.id
            ed.isAbstract = ett.isAbstract
            ed.extendsId = ett.extendsId
            result.customEntities.add(ed)
            ett.properties.values.forEach {
                val pd = CustomPropertyDescriptionDT()
                pd.id = it.id
                pd.className = it.className
                pd.lateInit  = it.lateinit
                pd.nonNullable = it.nonNullable
                pd.type = when(it.type){
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM ->CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
                ed.properties.add(pd)
            }
            ett.collections.values.forEach {
                val cd = CustomCollectionDescriptionDT()
                ed.collections.add(cd)
                cd.id = it.id
                cd.elementClassName = it.elementClassName
                cd.elementType  = when(it.elementType){
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM ->CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
            }
            ett.maps.values.forEach {cmd ->
                val md = CustomMapDescriptionDT()
                md.id = cmd.id
                ed.maps.add(md)
                md.keyClassName = cmd.keyClassName
                md.valueClassName = cmd.valueClassName
                md.keyClassType  = when(cmd.keyClassType){
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM ->CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
                md.valueClassType  = when(cmd.valueClassType){
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM ->CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
            }
        }
    }

    override fun service(request: GetMetadataRequest, ctx: RestOperationContext): GetMetadataResponse {
        return coreResponse.value
    }

}