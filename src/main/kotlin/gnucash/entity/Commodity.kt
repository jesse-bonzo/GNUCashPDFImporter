package gnucash.entity

import gnucash.guid

data class Commodity(
    override var guid: String = guid(),
    var namespace: String,
    var mnemonic: String,
    var fullname: String? = null,
    var cusip: String? = null,
    var fraction: Int,
    var quoteFlag: Int = 0,
    var quoteSource: String? = null,
    var quoteTz: String? = null
) : Entity