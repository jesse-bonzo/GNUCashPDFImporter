package gnucash.dao

import mu.KotlinLogging
import org.sqlite.SQLiteConfig
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class ConnectionProvider(private val connectionString: String) : AutoCloseable {

    private val connections = mutableListOf<Connection>()
    private val defaultProperties = SQLiteConfig().apply {
        setSynchronous(SQLiteConfig.SynchronousMode.FULL)
        setEncoding(SQLiteConfig.Encoding.UTF8)
    }.toProperties()

    @Synchronized
    fun getConnection(properties: Properties = defaultProperties): Connection {
        connections.removeIf { it.isClosed }

        return DriverManager.getConnection(connectionString, properties).apply {
            connections += this
        }.also {
            log.debug { "Total connections: ${connections.size}" }
        }
    }

    @Synchronized
    override fun close() {
        connections.removeIf { it.isClosed }

        log.debug { "Closing connections: ${connections.size}" }
        connections.forEach { connection ->
            try {
                connection.close()
            } catch (exception: Exception) {
                log.error { exception }
            }
        }
    }

    companion object {
        @JvmStatic
        private val log = KotlinLogging.logger { }
    }
}