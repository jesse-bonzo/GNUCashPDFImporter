package gnucash.dao

import gnucash.DATETIME_FORMAT
import gnucash.entity.Account
import gnucash.entity.Split
import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDateTime

class SplitDao : BaseDao<Split>() {
    override val table = "splits"
    override val columns = listOf(
        "guid",
        "tx_guid",
        "account_guid",
        "memo",
        "action",
        "reconcile_state",
        "reconcile_date",
        "value_num",
        "value_denom",
        "quantity_num",
        "quantity_denom",
        "lot_guid"
    )

    override fun createEntity(resultSet: ResultSet): Split {
        val guid = resultSet.getString("guid")
        val txGuid = resultSet.getString("tx_guid")
        val accountGuid = resultSet.getString("account_guid")
        val memo = resultSet.getString("memo")
        val action = resultSet.getString("action")
        val reconcileState = resultSet.getString("reconcile_state")
        val reconcileDate = resultSet.getString("reconcile_date")?.let { LocalDateTime.parse(it, DATETIME_FORMAT) }
        val valueNum = resultSet.getLong("value_num")
        val valueDenom = resultSet.getLong("value_denom")
        val quantityNum = resultSet.getLong("quantity_num")
        val quantityDenom = resultSet.getLong("quantity_denom")
        val lotGuid = resultSet.getString("lot_guid")
        return Split(
            guid,
            txGuid,
            accountGuid,
            memo,
            action,
            reconcileState,
            reconcileDate,
            valueNum,
            valueDenom,
            quantityNum,
            quantityDenom,
            lotGuid
        )
    }

    override fun toMap(entity: Split) = mapOf(
        "guid" to entity.guid,
        "tx_guid" to entity.txGuid,
        "account_guid" to entity.accountGuid,
        "memo" to entity.memo,
        "action" to entity.action,
        "reconcile_state" to entity.reconcileState,
        "reconcile_date" to entity.reconcileDate?.let { DATETIME_FORMAT.format(it) },
        "value_num" to entity.valueNum,
        "value_denom" to entity.valueDenom,
        "quantity_num" to entity.quantityNum,
        "quantity_denom" to entity.quantityDenom,
        "lot_guid" to entity.lotGuid
    )

    fun findByAccount(connection: Connection, account: Account) = with(connection) {
        findBy(mapOf("account_guid" to account.guid))
    }
}