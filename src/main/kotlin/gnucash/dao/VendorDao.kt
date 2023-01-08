package gnucash.dao

import gnucash.entity.Vendor
import java.sql.ResultSet

class VendorDao : BaseDao<Vendor>() {
    override val table = "vendors"
    override val columns = listOf(
        "guid",
        "name",
        "id",
        "notes",
        "currency",
        "active",
        "tax_override",
        "addr_name",
        "addr_addr1",
        "addr_addr2",
        "addr_addr3",
        "addr_addr4",
        "addr_phone",
        "addr_fax",
        "addr_email",
        "terms",
        "tax_inc",
        "tax_table"
    )

    override fun createEntity(resultSet: ResultSet): Vendor {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val id = resultSet.getString("id")
        val notes = resultSet.getString("notes")
        val currency = resultSet.getString("currency")
        val active = resultSet.getInt("active")
        val taxOverride = resultSet.getInt("tax_override")
        val addrName = resultSet.getString("addr_name")
        val addrAddr1 = resultSet.getString("addr_addr1")
        val addrAddr2 = resultSet.getString("addr_addr2")
        val addrAddr3 = resultSet.getString("addr_addr3")
        val addrAddr4 = resultSet.getString("addr_addr4")
        val addrPhone = resultSet.getString("addr_phone")
        val addrFax = resultSet.getString("addr_fax")
        val addrEmail = resultSet.getString("addr_email")
        val terms = resultSet.getString("terms")
        val taxInc = resultSet.getString("tax_inc")
        val taxTable = resultSet.getString("tax_table")
        return Vendor(
            guid,
            name,
            id,
            notes,
            currency,
            active,
            taxOverride,
            addrName,
            addrAddr1,
            addrAddr2,
            addrAddr3,
            addrAddr4,
            addrPhone,
            addrFax,
            addrEmail,
            terms,
            taxInc,
            taxTable
        )
    }
}