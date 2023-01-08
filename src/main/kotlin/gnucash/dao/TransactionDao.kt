package gnucash.dao

import gnucash.DATETIME_FORMAT
import gnucash.entity.Transaction
import java.sql.ResultSet
import java.time.LocalDateTime

class TransactionDao : BaseDao<Transaction>() {
    override val table = "transactions"

    override val columns = listOf("guid", "currency_guid", "num", "post_date", "enter_date", "description")

    override fun createEntity(resultSet: ResultSet): Transaction {
        val guid = resultSet.getString("guid")
        val currencyGuid = resultSet.getString("currency_guid")
        val num = resultSet.getString("num")
        val postDate = resultSet.getString("post_date")?.let { LocalDateTime.parse(it, DATETIME_FORMAT) }
        val enterDate = resultSet.getString("enter_date")?.let { LocalDateTime.parse(it, DATETIME_FORMAT) }
        val description = resultSet.getString("description")
        return Transaction(guid, currencyGuid, num, postDate, enterDate, description)
    }

    override fun toMap(entity: Transaction) = mapOf(
        "guid" to entity.guid,
        "currency_guid" to entity.currencyGuid,
        "num" to entity.num,
        "post_date" to entity.postDate?.let { DATETIME_FORMAT.format(it) },
        "enter_date" to entity.enterDate?.let { DATETIME_FORMAT.format(it) },
        "description" to entity.description
    )
}