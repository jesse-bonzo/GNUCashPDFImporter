package gnucash.dao

import gnucash.entity.Employee
import java.sql.ResultSet

object EmployeeDao : BaseDao<Employee>() {
    override val table = "employees"
    override val columns = listOf(
        "guid",
        "username",
        "id",
        "language",
        "acl",
        "active",
        "currency",
        "ccard_guid",
        "workday_num",
        "workday_denom",
        "rate_num",
        "rate_denom",
        "addr_name",
        "addr_addr1",
        "addr_addr2",
        "addr_addr3",
        "addr_addr4",
        "addr_phone",
        "addr_fax",
        "addr_email"
    )

    override fun createEntity(resultSet: ResultSet): Employee {
        val guid = resultSet.getString("guid")
        val username = resultSet.getString("username")
        val id = resultSet.getString("id")
        val language = resultSet.getString("language")
        val acl = resultSet.getString("acl")
        val active = resultSet.getInt("active")
        val currency = resultSet.getString("currency")
        val ccardGuid = resultSet.getString("ccard_guid")
        val workdayNum = resultSet.getLong("workday_num")
        val workdayDenom = resultSet.getLong("workday_denom")
        val rateNum = resultSet.getLong("rate_num")
        val rateDenom = resultSet.getLong("rate_denom")
        val addrName = resultSet.getString("addr_name")
        val addrAddr1 = resultSet.getString("addr_addr1")
        val addrAddr2 = resultSet.getString("addr_addr2")
        val addrAddr3 = resultSet.getString("addr_addr3")
        val addrAddr4 = resultSet.getString("addr_addr4")
        val addrPhone = resultSet.getString("addr_phone")
        val addrFax = resultSet.getString("addr_fax")
        val addrEmail = resultSet.getString("addr_email")
        return Employee(
            guid,
            username,
            id,
            language,
            acl,
            active,
            currency,
            ccardGuid,
            workdayNum,
            workdayDenom,
            rateNum,
            rateDenom,
            addrName,
            addrAddr1,
            addrAddr2,
            addrAddr3,
            addrAddr4,
            addrPhone,
            addrFax,
            addrEmail
        )
    }
}