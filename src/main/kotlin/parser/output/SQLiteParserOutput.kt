package parser.output

import configuration.AccountsConfiguration
import gnucash.dao.*
import gnucash.entity.Account
import gnucash.entity.Split
import gnucash.entity.Transaction
import gnucash.inTransaction
import mu.KotlinLogging
import java.io.File
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalTime

class SQLiteParserOutput(outputFile: File, private val accountsConfiguration: AccountsConfiguration) : ParserOutput {
    private val connectionProvider = ConnectionProvider("jdbc:sqlite:${outputFile.absolutePath}")
    override fun write(outputTransaction: OutputTransaction) {
        if (outputTransaction.creditAccount == null && outputTransaction.debitAccount == null) {
            throw Exception("No account was associated with this transaction: $outputTransaction")
        }

        val connection = connectionProvider.getConnection()

        val debitAccountConfig = accountsConfiguration.accounts.find {
            it.last4 == outputTransaction.debitAccount?.takeLast(4)
        }?.apply {
            createAccountIfNeeded(connection, this)
        }

        val creditAccountConfig = accountsConfiguration.accounts.find {
            it.last4 == outputTransaction.creditAccount?.takeLast(4)
        }?.apply {
            createAccountIfNeeded(connection, this)
        }

        if (creditAccountConfig == null && debitAccountConfig == null) {
            // we need at least one configured
            if (outputTransaction.creditAccount != null) {
                throw Exception("No configuration found for account: ${outputTransaction.creditAccount}")
            } else if (outputTransaction.debitAccount != null) {
                throw Exception("No configuration found for account: ${outputTransaction.debitAccount}")
            }
        }

        var debitAccount = debitAccountConfig?.name?.split(':')?.let { AccountDao.findAccount(connection, it) }
        val creditAccount = creditAccountConfig?.name?.split(':')?.let { AccountDao.findAccount(connection, it) }

        if (debitAccount == null && creditAccount != null) {
            // assume Expenses:Miscellaneous
            debitAccount = AccountDao.findAccount(connection, listOf("Expenses", "Miscellaneous"))
        }

        if (creditAccount == null && debitAccount != null) {
            // TODO: Can we make an assumption here?
        }

        val commodity = (creditAccount?.commodityGuid ?: debitAccount?.commodityGuid)?.let { commodityGuid ->
            CommodityDao.findByGuid(connection, commodityGuid)
        } ?: throw Exception("Unable to determine commodity to use: $outputTransaction")

        val tx = Transaction(
            currencyGuid = commodity.guid,
            postDate = outputTransaction.postDate.atTime(LocalTime.NOON),
            description = outputTransaction.description
        )

        val transactionAmount = outputTransaction.amount.toPennies()

        val debitSplit = Split(
            txGuid = tx.guid,//
            accountGuid = debitAccount?.guid.orEmpty(),
            valueNum = transactionAmount, // pennies
            valueDenom = 100,//
            quantityNum = transactionAmount, // TODO: would be shares * 10000 for stock transactions
            quantityDenom = 100 // would be 10000 for stock transactions
        )

        val creditSplit = Split(
            txGuid = tx.guid,//
            accountGuid = creditAccount?.guid.orEmpty(),
            valueNum = -transactionAmount, // pennies, negative of other split
            valueDenom = 100,//
            quantityNum = -transactionAmount, // pennies, same as valueNum
            quantityDenom = 100
        )

        connection.inTransaction {
            log.info(SplitDao.save(connection, debitSplit).toString())
            log.info(SplitDao.save(connection, creditSplit).toString())
            log.info(TransactionDao.save(connection, tx).toString())
        }
    }

    private fun createAccountIfNeeded(connection: Connection, accountConfig: AccountsConfiguration.Account) {
        val book = BookDao.getBook(connection)
        val rootAccount = AccountDao.getRootAccount(connection, book)
        val searchPath = accountConfig.name.split(':')

        val commodity = CommodityDao.findCurrencyByMnemonic(connection, accountConfig.commodity.name)
            ?: throw Exception("Unable to find commodity: ${accountConfig.commodity}")

        val parentAccount = AccountDao.findAccount(connection, searchPath.dropLast(1), rootAccount)
            ?: throw Exception("Can not find account, please create it: ${searchPath.dropLast(1).joinToString(":")}")

        val account = AccountDao.findAccount(connection, searchPath, rootAccount)
        if (account == null) {
            // create it!
            val saved = AccountDao.save(
                connection, Account(
                    name = searchPath.last(),
                    accountType = accountConfig.accountType.name,
                    commodityGuid = commodity.guid,
                    commodityScu = 100, // TODO: Not sure what this is
                    nonStdScu = 0, // TODO: Not sure what this is
                    parentGuid = parentAccount.guid,
                    code = accountConfig.last4,
                    description = accountConfig.description,
                    hidden = null,
                    placeholder = null
                )
            )
            log.info("Created account: $saved")
        }
    }

    override fun close() {
        connectionProvider.close()
    }

    companion object {
        @JvmStatic
        private val log = KotlinLogging.logger { }

        @JvmStatic
        private val ONE_HUNDRED = BigDecimal.valueOf(100)

        fun BigDecimal.toPennies() = this.multiply(ONE_HUNDRED).toLong()
    }
}