package gnucash.dao

import gnucash.entity.Book
import java.sql.Connection
import java.sql.ResultSet

object BookDao : BaseDao<Book>() {
    override val table = "books"
    override val columns = listOf("guid", "root_account_guid", "root_template_guid")
    override fun createEntity(resultSet: ResultSet): Book {
        val guid = resultSet.getString("guid")
        val rootAccountGuid = resultSet.getString("root_account_guid")
        val rootTemplateGuid = resultSet.getString("root_template_guid")
        return Book(guid, rootAccountGuid, rootTemplateGuid)
    }

    fun getBook(connection: Connection) = findAll(connection).singleOrNull() ?: throw Exception("Book not found")
}