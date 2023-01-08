package gnucash.entity

data class Recurrence(
    var id: Int,
    var objGuid: String,
    var recurrenceMult: Int,
    var recurrencePeriodType: String,
    var recurrencePeriodStart: String,
    var recurrenceWeekendAdjust: String
)