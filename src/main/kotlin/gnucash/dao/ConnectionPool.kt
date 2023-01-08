package gnucash.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class ConnectionPool(connectionString: String) : AutoCloseable {

    private val dataSource: HikariDataSource

    init {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = connectionString
        dataSource = HikariDataSource(hikariConfig)
    }

    fun getConnection(): Connection {
        return dataSource.connection
    }

    override fun close() {
        dataSource.close()
    }
}