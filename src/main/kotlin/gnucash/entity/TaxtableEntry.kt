package gnucash.entity

data class TaxtableEntry(
    var id: Int,
    var taxtable: String,
    var account: String,
    var amountNum: Long,
    var amountDenom: Long,
    var type: Int
)