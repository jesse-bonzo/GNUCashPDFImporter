package gnucash.dao

import gnucash.entity.Order
import java.sql.ResultSet

class OrderDao : BaseDao<Order>() {
    override val table = "orders"
    override val columns =
        listOf("guid", "id", "notes", "reference", "active", "date_opened", "date_closed", "owner_type", "owner_guid")

    override fun createEntity(resultSet: ResultSet): Order {
        val guid = resultSet.getString("guid")
        val id = resultSet.getString("id")
        val notes = resultSet.getString("notes")
        val reference = resultSet.getString("reference")
        val active = resultSet.getInt("active")
        val dateOpened = resultSet.getString("date_opened")
        val dateClosed = resultSet.getString("date_closed")
        val ownerType = resultSet.getInt("owner_type")
        val ownerGuid = resultSet.getString("owner_guid")
        return Order(guid, id, notes, reference, active, dateOpened, dateClosed, ownerType, ownerGuid)
    }
}