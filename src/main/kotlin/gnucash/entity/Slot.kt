package gnucash.entity

import java.time.LocalDate
import java.time.LocalDateTime

data class Slot(
    var id: Int,
    var objGuid: String,
    var name: String,
    var slotType: Int,
    var int64Val: Long?,
    var stringVal: String?,
    var doubleVal: Double?,
    var timespecVal: LocalDateTime?,
    var guidVal: String?,
    var numericValNum: Long?,
    var numericValDenom: Long?,
    var gdateVal: LocalDate?
)