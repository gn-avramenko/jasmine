/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.app.Environment
import org.slf4j.LoggerFactory


internal class DatabaseStructureAnalysisResult(val tablesToDelete: List<String>, val tablesToCreate:List<CreateTableData>, val tablesToUpdate: List<UpdateTableData>) {


    override fun toString(): String {
        if (tablesToDelete.isEmpty() && tablesToCreate.isEmpty()
                && tablesToUpdate.isEmpty()) {
            return "DatabaseStructureAnalysisResult: nothing to change"
        }
        val result = StringBuilder(
                "DatabaseStructureAnalysisResult:")
        result.append("\ntable to delete: $tablesToDelete")
        result.append("\ntable to create:")
        for (table in tablesToCreate) {
            result.append("\n${table.tableName}")
            result.append("\n\tcolumns:${table.columns.entries.joinToString { "\n\t\t${it.key}: ${it.value}"}}")
            result.append("\n\tindexes:${table.indexes.entries.joinToString { "\n\t\t${it.key}: ${it.value}"}}")
        }
        result.append("\ntable to update:")
        for (table in tablesToUpdate) {
            result.append("\n${table.tableName}:")
            result.append("\n\tcolumns to delete:${table.columnsToDelete.joinToString { "\n\t\t${it}" }}")
            result.append("\n\tindexes to delete:${table.indexesToDelete.joinToString { "\n\t\t${it}" }}")
            result.append("\n\tcolumns to create:${table.columnsToCreate.entries.joinToString { "\n\t\t${it.key}: ${it.value}" }}")
            result.append("\n\tindexes to create:${table.indexesToCreate.entries.joinToString { "\n\t\t${it.key}: ${it.value}" }}")
        }

        return result.toString()
    }

    class CreateTableData(val tableName: String, val columns: Map<String, SqlType>, val indexes: Map<String, JdbcIndexDescription>)

    class UpdateTableData(val tableName: String, val columnsToCreate: Map<String, SqlType>,val indexesToCreate: Map<String, JdbcIndexDescription>,val columnsToDelete: Set<String>,val indexesToDelete: Set<String> )
}

internal object DatabaseStructureAnalyzer {

    private val log = LoggerFactory.getLogger(javaClass)


    fun analyze(): DatabaseStructureAnalysisResult {
        val dialect = Environment.getPublished(JdbcDialect::class)
        val tablesToDelete = arrayListOf<String>()
        val tablesToCreate = arrayListOf<DatabaseStructureAnalysisResult.CreateTableData>()
        log.debug("START db structure analysis")
        val tableMappings = HashMap<String, DatabaseTableDescription>()
        val descriptions = JdbcUtils.getTableDescriptions()
        for (value in descriptions.values) {
            tableMappings[value.name.toLowerCase()] = value
        }
        val allTableNames = ArrayList(tableMappings.keys.map { it.toLowerCase() })
        val existingTableNames = LinkedHashSet(dialect.tableNames.map { it.toLowerCase() })
        tablesToDelete.addAll(disjunction(existingTableNames, allTableNames))
        for (tableName in disjunction(allTableNames,existingTableNames)) {
            val tableData = getTableData(tableMappings[tableName]!!)
            tablesToCreate.add(DatabaseStructureAnalysisResult.CreateTableData(tableName, columns = tableData.columns, indexes = tableData.indexes))
        }
        val tablesToUpdate = arrayListOf<DatabaseStructureAnalysisResult.UpdateTableData>()
        for (tableName in disjunction(existingTableNames, tablesToDelete)) {
            val existingColumns = dialect.getColumnTypes(tableName)
            val existingIndexes = LinkedHashSet<String>()
            existingIndexes.addAll(dialect.getIndexNames(tableName))
            val tableData = getTableData(tableMappings[tableName]!!)
            val columnsToDelete = disjunction(existingColumns.keys.map { it.toLowerCase() },tableData.columns.keys.map { it.toLowerCase() })
            val indexesToDelete = disjunction(existingIndexes.map { it.toLowerCase() },tableData.indexes.keys.map { it.toLowerCase() })
            val columnsToCreate = linkedMapOf<String, SqlType>()
            val indexesToCreate = linkedMapOf<String, JdbcIndexDescription>()
            for (column in disjunction(tableData.columns.keys.map { it.toLowerCase() },existingColumns.keys.map { it.toLowerCase() })) {
                columnsToCreate[column] = tableData.columns[column]!!
            }

            for (index in disjunction(tableData.indexes.keys.map { it.toLowerCase() },
                    existingIndexes.map { it.toLowerCase() })) {
                indexesToCreate[index] = tableData.indexes[index]!!
            }
            if (columnsToCreate.isEmpty() && columnsToDelete.isEmpty()
                    && indexesToCreate.isEmpty()
                    && indexesToDelete.isEmpty()) {
                continue
            }
            tablesToUpdate.add(DatabaseStructureAnalysisResult.UpdateTableData(tableName = tableName, columnsToCreate = columnsToCreate, columnsToDelete = columnsToDelete,
            indexesToCreate = indexesToCreate, indexesToDelete = indexesToDelete))
        }
        log.debug("END db structure analysis")
        return DatabaseStructureAnalysisResult(tablesToDelete=tablesToDelete, tablesToCreate = tablesToCreate, tablesToUpdate = tablesToUpdate)
    }

    private fun disjunction(all: Collection<String>,
                            toRemove: Collection<String>): LinkedHashSet<String> {
        val result = LinkedHashSet(all)
        result.removeAll(toRemove)
        return result
    }

    private fun getTableData(description: DatabaseTableDescription): TableData {
        val result = TableData()

        for (property in description.properties
                .values) {
            val handler = JdbcHandlerUtils.getPropertyHandler(property.type)
            val columns = handler
                    .getPropertyColumns(property)
            result.columns.putAll(columns)
            if (property.indexed) {
                result.indexes.putAll(handler
                        .getPropertyIndexes(property, description))
            }
        }
        for (coll in description.collections
                .values) {
            val handler = JdbcHandlerUtils.getCollectionHandler(coll.elementType)
            val columns = handler
                    .getCollectionColumns(coll)
            result.columns.putAll(columns)
            if (coll.indexed) {
                result.indexes.putAll(handler
                        .getCollectionIndexes(coll, description))
            }
        }

        keysToLowerCase(result.columns)
        keysToLowerCase(result.indexes)
        return result
    }

    private fun<T:Any> keysToLowerCase(columns: MutableMap<String, T>) {
        val result = LinkedHashMap<String,T>()
        columns.entries.forEach { result[it.key.toLowerCase()] =  it.value }
        columns.clear()
        columns.putAll(result)
    }


    class TableData {
        internal val columns: MutableMap<String, SqlType> = LinkedHashMap()

        internal val indexes: MutableMap<String, JdbcIndexDescription> = LinkedHashMap()
    }
}


object DatabaseStructureUpdater {


    private val log = LoggerFactory.getLogger(javaClass)



    fun updateDbStructure() {
        val result = DatabaseStructureAnalyzer.analyze()
        log.debug("structure change:\n$result")
        if(result.tablesToCreate.isEmpty() && result.tablesToDelete.isEmpty() && result.tablesToUpdate.isEmpty()){
            return
        }
        val dialect = Environment.getPublished(JdbcDialect::class)
            for (tableName in result.tablesToDelete) {
                JdbcUtils.update("drop table $tableName")
                log.info("table $tableName is deleted")
            }
            for (tableData in result.tablesToCreate) {
                JdbcUtils.update("CREATE TABLE ${tableData.tableName}(${tableData.columns.entries.joinToString(", ") { "${it.key} ${dialect.getSqlType(it.value)}" }})")
                log.info("table ${tableData.tableName} created")
                createIndexes(tableData.tableName, tableData.indexes)
            }
            for (tableData in result.tablesToUpdate) {
                for (index in tableData.indexesToDelete) {
                    JdbcUtils.update(dialect.createDropIndexQuery(tableData.tableName, index))
                    log.info("index $index of table ${tableData.tableName} deleted")
                }
                for (column in tableData.columnsToDelete) {
                    JdbcUtils.update("ALTER TABLE ${tableData.tableName} DROP COLUMN $column")
                    log.info("column $column of table ${tableData.tableName} deleted")
                }

                for (column in tableData.columnsToCreate.entries) {
                    JdbcUtils.update("ALTER TABLE ${tableData.tableName} ADD ${column.key} ${dialect.getSqlType(column.value)}")
                    log.info("column ${column.key} of type ${column.value} in table ${tableData.tableName} created")
                }
                createIndexes(tableData.tableName, tableData.indexesToCreate)
            }
    }

    private fun createIndexes(tableName: String, indexes: Map<String, JdbcIndexDescription>) {
        val dialect = Environment.getPublished(JdbcDialect::class)
        for ((key, value) in indexes) {
            JdbcUtils.update(dialect.getCreateIndexStatement(tableName, key, value))
            log.info("index $key on field $value of table $tableName created")
        }

    }

}
