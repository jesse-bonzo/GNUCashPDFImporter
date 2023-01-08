package gnucash.entity

data class Invoice(
    override var guid: String,
    var id: String,
    var dateOpened: String?,
    var datePosted: String?,
    var notes: String,
    var active: Int,
    var currency: String,
    var ownerType: Int?,
    var ownerGuid: String?,
    var terms: String?,
    var billingId: String?,
    var postTxn: String?,
    var postLot: String?,
    var postAcc: String?,
    var billtoType: Int?,
    var billtoGuid: String?,
    var chargeAmtNum: Long?,
    var chargeAmtDenom: Long?
) : Entity