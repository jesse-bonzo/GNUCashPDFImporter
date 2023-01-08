package gnucash.entity

import gnucash.guid
import java.time.LocalDateTime

data class Transaction(
    override var guid: String = guid(),
    var currencyGuid: String,
    var num: String = "",
    var postDate: LocalDateTime?,
    var enterDate: LocalDateTime? = LocalDateTime.now(),
    var description: String? = ""
) : Entity