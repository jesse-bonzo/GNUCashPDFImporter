package gnucash.entity

data class Billterm(
    override var guid: String,
    var name: String,
    var description: String,
    var refcount: Int,
    var invisible: Int,
    var parent: String?,
    var type: String,
    var duedays: Int?,
    var discountdays: Int?,
    var discountNum: Long?,
    var discountDenom: Long?,
    var cutoff: Int?
) : Entity