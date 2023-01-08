package gnucash.dao

import gnucash.entity.Lot
import java.sql.ResultSet

class LotDao : BaseDao<Lot>() {
    override val table = "lots"
    override val columns = listOf("guid", "account_guid", "is_closed")
    override fun createEntity(resultSet: ResultSet): Lot {
        val guid = resultSet.getString("guid")
        val accountGuid = resultSet.getString("account_guid")
        val isClosed = resultSet.getInt("is_closed")
        return Lot(guid, accountGuid, isClosed)
    }
}