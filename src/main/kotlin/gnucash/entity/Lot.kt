package gnucash.entity

data class Lot(override var guid: String, var accountGuid: String?, var isClosed: Int) : Entity