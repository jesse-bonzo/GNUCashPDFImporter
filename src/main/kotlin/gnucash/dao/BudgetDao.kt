package gnucash.dao

import gnucash.entity.Budget
import java.sql.ResultSet

class BudgetDao : BaseDao<Budget>() {
    override val table = "budgets"
    override val columns = listOf("guid", "name", "description", "num_periods")
    override fun createEntity(resultSet: ResultSet): Budget {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val description = resultSet.getString("description")
        val numPeriods = resultSet.getInt("num_periods")
        return Budget(guid, name, description, numPeriods)
    }
}