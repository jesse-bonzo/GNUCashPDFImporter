package configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


class Configuration(accountsYmlFile: String) {
    val accountsConfiguration = readAccountsConfiguration(accountsYmlFile)

    private fun readAccountsConfiguration(accountsYmlFile: String): AccountsConfiguration {
        val mapper = ObjectMapper(YAMLFactory()).findAndRegisterModules()
        return mapper.readValue(File(accountsYmlFile), AccountsConfiguration::class.java).apply {
            this.accounts.forEach { account ->
                if (account.last4.length != 4) {
                    throw Exception("Invalid configuration: $account")
                }
            }
        }
    }
}