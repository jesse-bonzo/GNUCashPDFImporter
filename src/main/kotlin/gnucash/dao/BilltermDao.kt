package gnucash.dao

import gnucash.entity.Billterm
import java.sql.ResultSet

class BilltermDao : BaseDao<Billterm>() {
    override val table = "billterms"
    override val columns = listOf(
        "guid",
        "name",
        "description",
        "refcount",
        "invisible",
        "parent",
        "type",
        "duedays",
        "discountdays",
        "discount_num",
        "discount_denom",
        "cutoff"
    )

    override fun createEntity(resultSet: ResultSet): Billterm {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val description = resultSet.getString("description")
        val refcount = resultSet.getInt("refcount")
        val invisible = resultSet.getInt("invisible")
        val parent = resultSet.getString("parent")
        val type = resultSet.getString("type")
        val duedays = resultSet.getInt("duedays")
        val discountdays = resultSet.getInt("discountdays")
        val discountNum = resultSet.getLong("discount_num")
        val discountDenom = resultSet.getLong("discount_denom")
        val cutoff = resultSet.getInt("cutoff")
        return Billterm(
            guid,
            name,
            description,
            refcount,
            invisible,
            parent,
            type,
            duedays,
            discountdays,
            discountNum,
            discountDenom,
            cutoff
        )
    }
}