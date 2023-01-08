package gnucash.entity

data class Taxtable(
    override var guid: String,
    var name: String,
    var refcount: Long,
    var invisible: Int,
    var parent: String?
) : Entity