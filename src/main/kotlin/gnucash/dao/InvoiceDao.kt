package gnucash.dao

import gnucash.entity.Invoice
import java.sql.ResultSet

class InvoiceDao : BaseDao<Invoice>() {
    override val table = "invoices"
    override val columns = listOf(
        "guid",
        "id",
        "date_opened",
        "date_posted",
        "notes",
        "active",
        "currency",
        "owner_type",
        "owner_guid",
        "terms",
        "billing_id",
        "post_txn",
        "post_lot",
        "post_acc",
        "billto_type",
        "billto_guid",
        "charge_amt_num",
        "charge_amt_denom"
    )

    override fun createEntity(resultSet: ResultSet): Invoice {
        val guid = resultSet.getString("guid")
        val id = resultSet.getString("id")
        val dateOpened = resultSet.getString("date_opened")
        val datePosted = resultSet.getString("date_posted")
        val notes = resultSet.getString("notes")
        val active = resultSet.getInt("active")
        val currency = resultSet.getString("currency")
        val ownerType = resultSet.getInt("owner_type")
        val ownerGuid = resultSet.getString("owner_guid")
        val terms = resultSet.getString("terms")
        val billingId = resultSet.getString("billing_id")
        val postTxn = resultSet.getString("post_txn")
        val postLot = resultSet.getString("post_lot")
        val postAcc = resultSet.getString("post_acc")
        val billtoType = resultSet.getInt("billto_type")
        val billtoGuid = resultSet.getString("billto_guid")
        val chargeAmtNum = resultSet.getLong("charge_amt_num")
        val chargeAmtDenom = resultSet.getLong("charge_amt_denom")
        return Invoice(
            guid,
            id,
            dateOpened,
            datePosted,
            notes,
            active,
            currency,
            ownerType,
            ownerGuid,
            terms,
            billingId,
            postTxn,
            postLot,
            postAcc,
            billtoType,
            billtoGuid,
            chargeAmtNum,
            chargeAmtDenom
        )
    }
}