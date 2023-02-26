package configuration

data class AccountsConfiguration(val accounts: List<Account>) {
    data class Account(
        val name: String,
        val last4: String,
        val commodity: Commodity,
        val accountType: AccountType,
        val description: String,
    ) {
        val namePath: List<String> by lazy {
            ACCOUNT_PATH_SPLITTER.split(name).toList()
        }

        enum class AccountType {
            `Credit Card`, Bank
        }

        enum class Commodity {
            USD
        }

        companion object {
            val ACCOUNT_PATH_SPLITTER = Regex(":")
        }
    }
}