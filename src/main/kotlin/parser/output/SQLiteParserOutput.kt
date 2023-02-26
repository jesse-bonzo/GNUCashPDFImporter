package parser.output

import configuration.AccountsConfiguration
import gnucash.dao.*
import gnucash.entity.Account
import gnucash.entity.Commodity
import gnucash.entity.Split
import gnucash.entity.Transaction
import mu.KotlinLogging
import java.io.File
import java.math.BigDecimal
import java.time.LocalTime

class SQLiteParserOutput(outputFile: File, private val accountsConfiguration: AccountsConfiguration) : ParserOutput {
    private val connectionProvider = ConnectionProvider("jdbc:sqlite:${outputFile.absolutePath}")
    private val connection by lazy { connectionProvider.getConnection() }
    private val book by lazy { BookDao.getBook(connection) }
    private val rootAccount by lazy { AccountDao.getRootAccount(connection, book) }
    private val commodities by lazy { CommodityDao.findAll(this.connection).associateBy { it.guid } }

    override fun write(outputTransaction: OutputTransaction) {
        val debitAccountConfig = accountsConfiguration.accounts.find {
            it.last4 == outputTransaction.debitAccount?.takeLast(4)
        }?.apply(::createAccountIfNeeded)

        val creditAccountConfig = accountsConfiguration.accounts.find {
            it.last4 == outputTransaction.creditAccount?.takeLast(4)
        }?.apply(::createAccountIfNeeded)

        if (creditAccountConfig == null && debitAccountConfig == null) {
            // we need at least one configured
            if (outputTransaction.creditAccount != null) {
                throw Exception("No configuration found for account: ${outputTransaction.creditAccount}")
            } else if (outputTransaction.debitAccount != null) {
                throw Exception("No configuration found for account: ${outputTransaction.debitAccount}")
            }
        }

        var debitAccount = debitAccountConfig?.namePath?.let { AccountDao.findAccount(connection, it) }
        var creditAccount = creditAccountConfig?.namePath?.let { AccountDao.findAccount(connection, it) }

        val commodity = (creditAccount?.commodityGuid ?: debitAccount?.commodityGuid)?.let { commodityGuid ->
            commodities[commodityGuid]
        } ?: throw Exception("Unable to determine commodity to use: $outputTransaction")

        if (outputTransaction.symbol != null && outputTransaction.price != null && outputTransaction.quantity != null) {
            // it's a stock purchase
            if (debitAccount == null && creditAccountConfig != null) {
                debitAccount = createStockAccountIfNeeded(creditAccountConfig, outputTransaction.symbol)
            }

            // it's a stock sell
            if (creditAccount == null && debitAccountConfig != null) {
                creditAccount = createStockAccountIfNeeded(debitAccountConfig, outputTransaction.symbol)
            }
        }

        if (debitAccount == null) {
            // assume Imbalance-COMMODITY
            debitAccount = getImbalanceAccount(commodity)
        }

        if (creditAccount == null) {
            // assume Imbalance-COMMODITY
            creditAccount = getImbalanceAccount(commodity)
        }

        val debitCommodity = debitAccount.commodityGuid?.let { CommodityDao.findByGuid(connection, it) } ?: commodity
        val creditCommodity = creditAccount.commodityGuid?.let { CommodityDao.findByGuid(connection, it) } ?: commodity

        val tx = save(
            Transaction(
                currencyGuid = commodity.guid,
                postDate = outputTransaction.postDate.atTime(neutralTime),
                description = outputTransaction.description.replace(whitespace, " "),
                num = outputTransaction.reference.orEmpty()
            )
        )

        val transactionAmount = outputTransaction.amount.multiply(BigDecimal(commodity.fraction)).toLong()

        val debitSplit = save(
            Split(
                txGuid = tx.guid,//
                accountGuid = debitAccount.guid,//
                valueNum = transactionAmount,//
                valueDenom = commodity.fraction.toLong(),//
                quantityNum = if (debitCommodity != commodity && outputTransaction.quantity != null) {
                    outputTransaction.quantity.multiply(BigDecimal(debitCommodity.fraction)).toLong()
                } else {
                    transactionAmount
                },
                quantityDenom = if (debitCommodity != commodity && outputTransaction.quantity != null) {
                    debitCommodity.fraction.toLong()
                } else {
                    commodity.fraction.toLong()
                }
            )
        )

        val creditSplit = save(
            Split(
                txGuid = tx.guid,//
                accountGuid = creditAccount.guid,
                valueNum = -transactionAmount, // pennies, negative of other split
                valueDenom = commodity.fraction.toLong(),//
                quantityNum = if (creditCommodity != commodity && outputTransaction.quantity != null) {
                    -outputTransaction.quantity.multiply(BigDecimal(creditCommodity.fraction)).toLong()
                } else {
                    -transactionAmount
                },
                quantityDenom = if (creditCommodity != commodity && outputTransaction.quantity != null) {
                    creditCommodity.fraction.toLong()
                } else {
                    commodity.fraction.toLong()
                }
            )
        )

        log.info { tx }
        log.info { debitSplit }
        log.info { creditSplit }
    }

    private fun save(transaction: Transaction) = TransactionDao.save(connection, transaction)
    private fun save(split: Split) = SplitDao.save(connection, split)

    private fun createAccountIfNeeded(accountConfig: AccountsConfiguration.Account): Account {
        return AccountDao.findAccount(connection, accountConfig.namePath, rootAccount) ?: createAccount(accountConfig)
    }

    private fun createStockAccountIfNeeded(
        parentAccountConfig: AccountsConfiguration.Account,
        symbol: String
    ): Account {
        val parentAccount = createAccountIfNeeded(parentAccountConfig)
        val stockAccount = AccountDao.findAccount(connection, symbol, parentAccount)
        return if (stockAccount == null) {
            var commodity = CommodityDao.findCurrencyByMnemonic(connection, symbol)
            if (commodity == null) {
                commodity = CommodityDao.save(
                    connection, Commodity(
                        namespace = "Stocks",
                        mnemonic = symbol,
                        fraction = 10_000
                    )
                )
            }
            val saved = AccountDao.save(
                connection, Account(
                    name = symbol,
                    accountType = "STOCK",
                    commodityGuid = commodity.guid,
                    commodityScu = commodity.fraction,
                    parentGuid = parentAccount.guid,
                    code = symbol,
                    description = symbol,
                )
            )
            log.info("Created account: $saved")
            saved
        } else {
            stockAccount
        }
    }

    private fun createAccount(accountConfig: AccountsConfiguration.Account): Account {
        val searchPath = accountConfig.namePath
        val parentSearchPath = searchPath.dropLast(1)
        val parentAccount = AccountDao.findAccount(connection, parentSearchPath, rootAccount)
            ?: throw Exception("Can not find account, please create it: ${parentSearchPath.joinToString(":")}")

        val commodity = CommodityDao.findCurrencyByMnemonic(connection, accountConfig.commodity.name)
            ?: throw Exception("Unable to find commodity: ${accountConfig.commodity}")

        // create it!
        val saved = AccountDao.save(
            connection, Account(
                name = searchPath.last(),
                accountType = accountConfig.accountType.name,
                commodityGuid = commodity.guid,
                commodityScu = commodity.fraction,
                parentGuid = parentAccount.guid,
                code = accountConfig.last4,
                description = accountConfig.description,
            )
        )
        log.info("Created account: $saved")
        return saved
    }

    private fun getImbalanceAccount(commodity: Commodity): Account {
        val accountName = "Imbalance-${commodity.mnemonic}"
        return AccountDao.findAccount(connection, accountName, rootAccount) ?: AccountDao.save(
            connection, Account(
                name = accountName,
                accountType = "BANK",
                commodityGuid = commodity.guid,
                commodityScu = commodity.fraction,
                parentGuid = rootAccount.guid,
                code = "",
                description = ""
            )
        )
    }

    override fun close() {
        connectionProvider.close()
    }

    companion object {
        @JvmStatic
        private val log = KotlinLogging.logger { }

        private val whitespace = Regex("\\s+")

        // GNUCash uses this as the default time
        private val neutralTime = LocalTime.of(10, 59)
    }
}