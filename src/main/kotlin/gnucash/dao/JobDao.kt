package gnucash.dao

import gnucash.entity.Job
import java.sql.ResultSet

object JobDao : BaseDao<Job>() {
    override val table = "jobs"
    override val columns = listOf("guid", "id", "name", "reference", "active", "owner_type", "owner_guid")
    override fun createEntity(resultSet: ResultSet): Job {
        val guid = resultSet.getString("guid")
        val id = resultSet.getString("id")
        val name = resultSet.getString("name")
        val reference = resultSet.getString("reference")
        val active = resultSet.getInt("active")
        val ownerType = resultSet.getInt("owner_type")
        val ownerGuid = resultSet.getString("owner_guid")
        return Job(guid, id, name, reference, active, ownerType, ownerGuid)
    }
}