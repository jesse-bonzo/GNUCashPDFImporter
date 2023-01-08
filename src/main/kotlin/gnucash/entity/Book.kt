package gnucash.entity

data class Book(override var guid: String, var rootAccountGuid: String, var rootTemplateGuid: String) : Entity