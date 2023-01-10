package configuration

data class AccountsConfiguration(val accounts: List<Account>) {
    data class Account(
        val name: String,
        val last4: String,
        val commodity: Commodity,
        val accountType: AccountType,
        val description: String,
    )

    enum class AccountType {
        `Credit Card`, Bank
    }

    enum class Commodity {
        USD
    }
}