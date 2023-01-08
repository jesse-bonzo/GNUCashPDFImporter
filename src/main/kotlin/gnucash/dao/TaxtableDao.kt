package gnucash.dao

import gnucash.entity.Taxtable
import java.sql.ResultSet

class TaxtableDao : BaseDao<Taxtable>() {
    override val table = "taxtables"
    override val columns = listOf("guid", "name", "refcount", "invisible", "parent")
    override fun createEntity(resultSet: ResultSet): Taxtable {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val refcount = resultSet.getLong("refcount")
        val invisible = resultSet.getInt("invisible")
        val parent = resultSet.getString("parent")
        return Taxtable(guid, name, refcount, invisible, parent)
    }
}