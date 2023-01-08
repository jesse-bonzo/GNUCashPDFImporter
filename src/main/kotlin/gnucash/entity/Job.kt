package gnucash.entity

data class Job(
    override var guid: String,
    var id: String,
    var name: String,
    var reference: String,
    var active: Int,
    var ownerType: Int?,
    var ownerGuid: String?
) : Entity