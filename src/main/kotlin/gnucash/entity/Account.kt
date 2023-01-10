package gnucash.entity

import gnucash.guid

data class Account(
    override var guid: String = guid(),
    var name: String,
    var accountType: String,
    var commodityGuid: String?,
    var commodityScu: Int,
    var nonStdScu: Int,
    var parentGuid: String?,
    var code: String?,
    var description: String?,
    var hidden: Int?,
    var placeholder: Int?
) : Entity