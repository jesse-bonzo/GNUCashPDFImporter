package gnucash.dao

import gnucash.entity.Price
import java.sql.ResultSet

object PriceDao : BaseDao<Price>() {
    override val table = "prices"
    override val columns =
        listOf("guid", "commodity_guid", "currency_guid", "date", "source", "type", "value_num", "value_denom")

    override fun createEntity(resultSet: ResultSet): Price {
        val guid = resultSet.getString("guid")
        val commodityGuid = resultSet.getString("commodity_guid")
        val currencyGuid = resultSet.getString("currency_guid")
        val date = resultSet.getString("date")
        val source = resultSet.getString("source")
        val type = resultSet.getString("type")
        val valueNum = resultSet.getLong("value_num")
        val valueDenom = resultSet.getLong("value_denom")
        return Price(guid, commodityGuid, currencyGuid, date, source, type, valueNum, valueDenom)
    }
}