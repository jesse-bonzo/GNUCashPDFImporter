package gnucash.dao

import gnucash.entity.Entity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

abstract class BaseDao<T : Entity> {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    abstract val table: String

    abstract val columns: List<String>

    protected abstract fun createEntity(resultSet: ResultSet): T

    protected open fun toMap(entity: T): Map<String, Any?> {
        TODO("IMPLEMENT ME")
    }

    private val selectClause by lazy { columns.joinToString(", ") }

    fun save(connection: Connection, entity: T): T {
        val entityMap = toMap(entity)
        if (!columns.containsAll(entityMap.keys)) {
            throw IllegalArgumentException("Invalid columns: $entity")
        }

        val insertColumns = entityMap.keys.joinToString(", ")
        val insertValues = entityMap.values.joinToString(", ") { if (it is String) "\"$it\"" else it.toString() }
        connection.prepareStatement("insert into $table ($insertColumns) values ($insertValues)").use { statement ->
            log.info(statement.toString())

            val result = statement.executeUpdate()
            if (result == 0) {
                throw SQLException("Unable to save $entity")
            }
        }
        return entity
    }

    fun delete(connection: Connection, entity: T): T {
        connection.prepareStatement("delete from $table where guid=${entity.guid}").use { statement ->
            log.info(statement.toString())

            val result = statement.executeUpdate()
            if (result == 0) {
                throw SQLException("Unable to delete $entity")
            }
        }
        return entity
    }

    fun findAll(connection: Connection): List<T> {
        val entities = mutableListOf<T>()
        connection.prepareStatement("select $selectClause from $table").use { statement ->
            log.info(statement.toString())

            statement.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    entities += createEntity(resultSet)
                }
            }
        }
        return entities
    }

    fun findByGuid(connection: Connection, guid: String): T? {
        return if ("guid" in columns) {
            connection.findBy(mapOf("guid" to guid)).firstOrNull()
        } else {
            null
        }
    }

    protected fun Connection.findBy(parameters: Map<String, Any>): List<T> {
        val whereClause = parameters.filter {
            it.key in columns
        }.entries.joinToString(separator = " and ") { (key, value) ->
            if (value is String) "$key = \"$value\"" else "$key = $value"
        }

        return if (whereClause.isEmpty()) {
            listOf()
        } else {
            val entities = mutableListOf<T>()
            prepareStatement("select $selectClause from $table where $whereClause").use { statement ->
                log.info(statement.toString())
                
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        entities += createEntity(resultSet)
                    }
                }
            }
            entities
        }
    }
}