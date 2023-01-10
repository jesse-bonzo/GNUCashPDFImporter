package gnucash.dao

import gnucash.entity.Commodity
import java.sql.Connection
import java.sql.ResultSet

object CommodityDao : BaseDao<Commodity>() {
    override val table = "commodities"
    override val columns = listOf(
        "guid", "namespace", "mnemonic", "fullname", "cusip", "fraction", "quote_flag", "quote_source", "quote_tz"
    )

    override fun createEntity(resultSet: ResultSet): Commodity {
        val guid = resultSet.getString("guid")
        val namespace = resultSet.getString("namespace")
        val mnemonic = resultSet.getString("mnemonic")
        val fullname = resultSet.getString("fullname")
        val cusip = resultSet.getString("cusip")
        val fraction = resultSet.getInt("fraction")
        val quoteFlag = resultSet.getInt("quote_flag")
        val quoteSource = resultSet.getString("quote_source")
        val quoteTz = resultSet.getString("quote_tz")
        return Commodity(guid, namespace, mnemonic, fullname, cusip, fraction, quoteFlag, quoteSource, quoteTz)
    }

    fun findCurrencyByMnemonic(connection: Connection, mnemonic: String) = with(connection) {
        findBy(mapOf("namespace" to "CURRENCY", "mnemonic" to mnemonic)).firstOrNull()
    }
}