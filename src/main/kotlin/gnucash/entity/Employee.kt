package gnucash.entity

data class Employee(
    override var guid: String,
    var username: String,
    var id: String,
    var language: String,
    var acl: String,
    var active: Int,
    var currency: String,
    var ccardGuid: String?,
    var workdayNum: Long,
    var workdayDenom: Long,
    var rateNum: Long,
    var rateDenom: Long,
    var addrName: String?,
    var addrAddr1: String?,
    var addrAddr2: String?,
    var addrAddr3: String?,
    var addrAddr4: String?,
    var addrPhone: String?,
    var addrFax: String?,
    var addrEmail: String?
) : Entity