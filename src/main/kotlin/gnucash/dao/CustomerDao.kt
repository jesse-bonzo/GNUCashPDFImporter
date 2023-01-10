package gnucash.dao

import gnucash.entity.Customer
import java.sql.ResultSet

object CustomerDao : BaseDao<Customer>() {
    override val table = "customers"
    override val columns = listOf(
        "guid",
        "name",
        "id",
        "notes",
        "active",
        "discount_num",
        "discount_denom",
        "credit_num",
        "credit_denom",
        "currency",
        "tax_override",
        "addr_name",
        "addr_addr1",
        "addr_addr2",
        "addr_addr3",
        "addr_addr4",
        "addr_phone",
        "addr_fax",
        "addr_email",
        "shipaddr_name",
        "shipaddr_addr1",
        "shipaddr_addr2",
        "shipaddr_addr3",
        "shipaddr_addr4",
        "shipaddr_phone",
        "shipaddr_fax",
        "shipaddr_email",
        "terms",
        "tax_included",
        "taxtable"
    )

    override fun createEntity(resultSet: ResultSet): Customer {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val id = resultSet.getString("id")
        val notes = resultSet.getString("notes")
        val active = resultSet.getInt("active")
        val discountNum = resultSet.getLong("discount_num")
        val discountDenom = resultSet.getLong("discount_denom")
        val creditNum = resultSet.getLong("credit_num")
        val creditDenom = resultSet.getLong("credit_denom")
        val currency = resultSet.getString("currency")
        val taxOverride = resultSet.getInt("tax_override")
        val addrName = resultSet.getString("addr_name")
        val addrAddr1 = resultSet.getString("addr_addr1")
        val addrAddr2 = resultSet.getString("addr_addr2")
        val addrAddr3 = resultSet.getString("addr_addr3")
        val addrAddr4 = resultSet.getString("addr_addr4")
        val addrPhone = resultSet.getString("addr_phone")
        val addrFax = resultSet.getString("addr_fax")
        val addrEmail = resultSet.getString("addr_email")
        val shipaddrName = resultSet.getString("shipaddr_name")
        val shipaddrAddr1 = resultSet.getString("shipaddr_addr1")
        val shipaddrAddr2 = resultSet.getString("shipaddr_addr2")
        val shipaddrAddr3 = resultSet.getString("shipaddr_addr3")
        val shipaddrAddr4 = resultSet.getString("shipaddr_addr4")
        val shipaddrPhone = resultSet.getString("shipaddr_phone")
        val shipaddrFax = resultSet.getString("shipaddr_fax")
        val shipaddrEmail = resultSet.getString("shipaddr_email")
        val terms = resultSet.getString("terms")
        val taxIncluded = resultSet.getInt("tax_included")
        val taxtable = resultSet.getString("taxtable")
        return Customer(
            guid,
            name,
            id,
            notes,
            active,
            discountNum,
            discountDenom,
            creditNum,
            creditDenom,
            currency,
            taxOverride,
            addrName,
            addrAddr1,
            addrAddr2,
            addrAddr3,
            addrAddr4,
            addrPhone,
            addrFax,
            addrEmail,
            shipaddrName,
            shipaddrAddr1,
            shipaddrAddr2,
            shipaddrAddr3,
            shipaddrAddr4,
            shipaddrPhone,
            shipaddrFax,
            shipaddrEmail,
            terms,
            taxIncluded,
            taxtable
        )
    }
}