package gnucash.entity

data class Schedxaction(
    override var guid: String,
    var name: String?,
    var enabled: Int,
    var startDate: String?,
    var endDate: String?,
    var lastOccur: String?,
    var numOccur: Int,
    var remOccur: Int,
    var autoCreate: Int,
    var autoNotify: Int,
    var advCreation: Int,
    var advNotify: Int,
    var instanceCount: Int,
    var templateActGuid: String
) : Entity