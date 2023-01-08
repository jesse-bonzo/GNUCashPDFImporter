package gnucash.entity

data class Order(
    override var guid: String,
    var id: String,
    var notes: String,
    var reference: String,
    var active: Int,
    var dateOpened: String,
    var dateClosed: String,
    var ownerType: Int,
    var ownerGuid: String
) : Entity