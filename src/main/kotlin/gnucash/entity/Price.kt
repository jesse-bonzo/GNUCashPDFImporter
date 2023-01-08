package gnucash.entity

data class Price(
    override var guid: String,
    var commodityGuid: String,
    var currencyGuid: String,
    var date: String,
    var source: String?,
    var type: String?,
    var valueNum: Long,
    var valueDenom: Long
) : Entity