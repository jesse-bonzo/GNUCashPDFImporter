package gnucash.entity

data class Commodity(
    override var guid: String,
    var namespace: String,
    var mnemonic: String,
    var fullname: String?,
    var cusip: String?,
    var fraction: Int,
    var quoteFlag: Int,
    var quoteSource: String?,
    var quoteTz: String?
) : Entity