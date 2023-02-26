package gnucash.dao

import gnucash.entity.Account
import gnucash.entity.Book
import java.sql.Connection
import java.sql.ResultSet

object AccountDao : BaseDao<Account>() {
    override val table = "accounts"
    override val columns = listOf(
        "guid",
        "name",
        "account_type",
        "commodity_guid",
        "commodity_scu",
        "non_std_scu",
        "parent_guid",
        "code",
        "description",
        "hidden",
        "placeholder"
    )

    override fun createEntity(resultSet: ResultSet): Account {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val accountType = resultSet.getString("account_type")
        val commodityGuid = resultSet.getString("commodity_guid")
        val commodityScu = resultSet.getInt("commodity_scu")
        val nonStdScu = resultSet.getInt("non_std_scu")
        val parentGuid = resultSet.getString("parent_guid")
        val code = resultSet.getString("code")
        val description = resultSet.getString("description")
        val hidden = resultSet.getInt("hidden")
        val placeholder = resultSet.getInt("placeholder")
        return Account(
            guid,
            name,
            accountType,
            commodityGuid,
            commodityScu,
            nonStdScu,
            parentGuid,
            code,
            description,
            hidden,
            placeholder
        )
    }

    override fun toMap(entity: Account) = mapOf(
        "guid" to entity.guid,
        "name" to entity.name,
        "account_type" to entity.accountType,
        "commodity_guid" to entity.commodityGuid,
        "commodity_scu" to entity.commodityScu,
        "non_std_scu" to entity.nonStdScu,
        "parent_guid" to entity.parentGuid,
        "code" to entity.code,
        "description" to entity.description,
        "hidden" to entity.hidden,
        "placeholder" to entity.placeholder
    )

    fun findChildrenAccounts(connection: Connection, parent: Account) = with(connection) {
        findBy(mapOf("parent_guid" to parent.guid))
    }

    fun findByName(connection: Connection, name: String) = with(connection) {
        findBy(mapOf("name" to name))
    }

    fun findChildrenByName(connection: Connection, parent: Account, name: String) = with(connection) {
        findBy(mapOf("parent_guid" to parent.guid, "name" to name))
    }

    fun findAccount(connection: Connection, name: String, parent: Account) = with(connection) {
        findBy(mapOf("name" to name, "parent_guid" to parent.guid)).singleOrNull()
    }

    fun findAccount(connection: Connection, namePath: List<String>, parent: Account? = null): Account? {
        if (namePath.isEmpty()) {
            return null
        }

        val accountName = namePath.first()
        val found = if (parent == null) {
            findByName(connection, accountName)
        } else {
            findChildrenByName(connection, parent, accountName)
        }

        if (namePath.size == 1 && found.size == 1) {
            return found.first()
        } else {
            for (account in found) {
                val result = findAccount(connection, namePath.drop(1), account)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    fun getRootAccount(connection: Connection, book: Book) =
        AccountDao.findByGuid(connection, book.rootAccountGuid) ?: throw Exception("Root account not found")
}