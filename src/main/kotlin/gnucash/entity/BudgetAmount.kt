package gnucash.entity

data class BudgetAmount(
    var id: Int,
    var budgetGuid: String,
    var accountGuid: String,
    var periodNum: Int,
    var amountNum: Long,
    var amountDenom: Long
)