package gnucash.entity

import gnucash.guid

data class Account(
    override var guid: String = guid(),
    var name: String,
    var accountType: String,
    var commodityGuid: String?,
    var commodityScu: Int,
    var nonStdScu: Int = 0,
    var parentGuid: String?,
    var code: String?,
    var description: String?,
    var hidden: Int? = 0,
    var placeholder: Int? = 0
) : Entity