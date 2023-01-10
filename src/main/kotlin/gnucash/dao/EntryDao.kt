package gnucash.dao

import gnucash.entity.Entry
import java.sql.ResultSet

object EntryDao : BaseDao<Entry>() {
    override val table = "entries"
    override val columns = listOf(
        "guid",
        "date",
        "date_entered",
        "description",
        "action",
        "notes",
        "quantity_num",
        "quantity_denom",
        "i_acct",
        "i_price_num",
        "i_price_denom",
        "i_discount_num",
        "i_discount_denom",
        "invoice",
        "i_disc_type",
        "i_disc_how",
        "i_taxable",
        "i_taxincluded",
        "i_taxtable",
        "b_acct",
        "b_price_num",
        "b_price_denom",
        "bill",
        "b_taxable",
        "b_taxincluded",
        "b_taxtable",
        "b_paytype",
        "billable",
        "billto_type",
        "billto_guid",
        "order_guid"
    )

    override fun createEntity(resultSet: ResultSet): Entry {
        val guid = resultSet.getString("guid")
        val date = resultSet.getString("date")
        val dateEntered = resultSet.getString("date_entered")
        val description = resultSet.getString("description")
        val action = resultSet.getString("action")
        val notes = resultSet.getString("notes")
        val quantityNum = resultSet.getLong("quantity_num")
        val quantityDenom = resultSet.getLong("quantity_denom")
        val iAcct = resultSet.getString("i_acct")
        val iPriceNum = resultSet.getLong("i_price_num")
        val iPriceDenom = resultSet.getLong("i_price_denom")
        val iDiscountNum = resultSet.getLong("i_discount_num")
        val iDiscountDenom = resultSet.getLong("i_discount_denom")
        val invoice = resultSet.getString("invoice")
        val iDiscType = resultSet.getString("i_disc_type")
        val iDiscHow = resultSet.getString("i_disc_how")
        val iTaxable = resultSet.getInt("i_taxable")
        val iTaxincluded = resultSet.getInt("i_taxincluded")
        val iTaxtable = resultSet.getString("i_taxtable")
        val bAcct = resultSet.getString("b_acct")
        val bPriceNum = resultSet.getLong("b_price_num")
        val bPriceDenom = resultSet.getLong("b_price_denom")
        val bill = resultSet.getString("bill")
        val bTaxable = resultSet.getInt("b_taxable")
        val bTaxincluded = resultSet.getInt("b_taxincluded")
        val bTaxtable = resultSet.getString("b_taxtable")
        val bPaytype = resultSet.getInt("b_paytype")
        val billable = resultSet.getInt("billable")
        val billtoType = resultSet.getInt("billto_type")
        val billtoGuid = resultSet.getString("billto_guid")
        val orderGuid = resultSet.getString("order_guid")
        return Entry(
            guid,
            date,
            dateEntered,
            description,
            action,
            notes,
            quantityNum,
            quantityDenom,
            iAcct,
            iPriceNum,
            iPriceDenom,
            iDiscountNum,
            iDiscountDenom,
            invoice,
            iDiscType,
            iDiscHow,
            iTaxable,
            iTaxincluded,
            iTaxtable,
            bAcct,
            bPriceNum,
            bPriceDenom,
            bill,
            bTaxable,
            bTaxincluded,
            bTaxtable,
            bPaytype,
            billable,
            billtoType,
            billtoGuid,
            orderGuid
        )
    }
}