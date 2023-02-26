package gnucash.entity

import gnucash.guid
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


data class Split(
    override var guid: String = guid(),
    var txGuid: String,
    var accountGuid: String,
    var memo: String = "",
    var action: String = "",
    var reconcileState: String = "n",
    var reconcileDate: LocalDateTime = DEFAULT_RECONCILE_DATE,
    var valueNum: Long,
    var valueDenom: Long,
    var quantityNum: Long,
    var quantityDenom: Long,
    var lotGuid: String? = null
) : Entity {
    companion object {
        val DEFAULT_RECONCILE_DATE: LocalDateTime = LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIDNIGHT)
    }
}