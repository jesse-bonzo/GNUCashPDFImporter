package gnucash.entity

data class Budget(override var guid: String, var name: String, var description: String?, var numPeriods: Int) : Entity