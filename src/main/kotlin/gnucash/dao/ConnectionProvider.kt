package gnucash.dao

import java.sql.Connection
import java.sql.DriverManager

class ConnectionProvider(connectionString: String) : AutoCloseable {

    // this might become problematic if we use multiple threads, but it's good enough for now
    private val connection_ by lazy {
        DriverManager.getConnection(connectionString).apply {
            this.autoCommit = false
        }
    }

    fun getConnection(): Connection {
        return connection_
    }

    override fun close() {
        connection_.close()
    }
}