package gnucash.entity

data class Vendor(
    override var guid: String,
    var name: String,
    var id: String,
    var notes: String,
    var currency: String,
    var active: Int,
    var taxOverride: Int,
    var addrName: String?,
    var addrAddr1: String?,
    var addrAddr2: String?,
    var addrAddr3: String?,
    var addrAddr4: String?,
    var addrPhone: String?,
    var addrFax: String?,
    var addrEmail: String?,
    var terms: String?,
    var taxInc: String?,
    var taxTable: String?
) : Entity