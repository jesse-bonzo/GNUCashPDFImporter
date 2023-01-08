package gnucash.dao

import gnucash.entity.Schedxaction
import java.sql.ResultSet

class SchedxactionDao : BaseDao<Schedxaction>() {
    override val table = "schedxactions"
    override val columns = listOf(
        "guid",
        "name",
        "enabled",
        "start_date",
        "end_date",
        "last_occur",
        "num_occur",
        "rem_occur",
        "auto_create",
        "auto_notify",
        "adv_creation",
        "adv_notify",
        "instance_count",
        "template_act_guid"
    )

    override fun createEntity(resultSet: ResultSet): Schedxaction {
        val guid = resultSet.getString("guid")
        val name = resultSet.getString("name")
        val enabled = resultSet.getInt("enabled")
        val startDate = resultSet.getString("start_date")
        val endDate = resultSet.getString("end_date")
        val lastOccur = resultSet.getString("last_occur")
        val numOccur = resultSet.getInt("num_occur")
        val remOccur = resultSet.getInt("rem_occur")
        val autoCreate = resultSet.getInt("auto_create")
        val autoNotify = resultSet.getInt("auto_notify")
        val advCreation = resultSet.getInt("adv_creation")
        val advNotify = resultSet.getInt("adv_notify")
        val instanceCount = resultSet.getInt("instance_count")
        val templateActGuid = resultSet.getString("template_act_guid")
        return Schedxaction(
            guid,
            name,
            enabled,
            startDate,
            endDate,
            lastOccur,
            numOccur,
            remOccur,
            autoCreate,
            autoNotify,
            advCreation,
            advNotify,
            instanceCount,
            templateActGuid
        )
    }
}