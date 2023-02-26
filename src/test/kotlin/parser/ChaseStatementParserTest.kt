package parser

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.junit.jupiter.api.Test
import parser.input.StringParserInput
import parser.output.CSVParserOutput
import java.nio.file.Files
import kotlin.test.assertEquals


class ChaseStatementParserTest {

    private val sampleText = """
         Manage your account online at: Mobile:  Download the
         Chase Mobile app today® 
         ACCOUNT SUMMARY
         YOUR ACCOUNT MESSAGES
         AUTOPAY IS ON
         P.O. BOX 15123
            WILMINGTON, DE 19850-5123
         For Undeliverable Mail Only
         ${'$'}
         Payment Due Date: 10/02/22
         New Balance: ${'$'}23.75
         Minimum Payment Due: ${'$'}23.75
         1212          
         11111 2222222 D 9 Y 9 05 22/09/05 Page 1 of 2 11111 MA MA 11111 1111111111111111111111 
         11111 BEX 9 24822 D 
          Customer Service:
         www.chase.com/cardhelp 1-800-524-3880
         Late Payment Warning:  
         Account Number:  1234 5678 9012 3456
         New Balance ${'$'}23.75
         Past Due Amount ${'$'}0.00
         Balance over the Credit Limit ${'$'}0.00
         Account number: 1234 5678 9012 3456
         AUTOPAY IS ON
         If we do not receive your minimum payment
         by the date listed above, you may have to pay a late fee of up to
         ${'$'}40.00 and your APR's will be subject to increase to a maximum
         Penalty APR of 29.99%.
         Previous Balance ${'$'}0.00
         Payment, Credits ${'$'}0.00
         Purchases +${'$'}23.75
         Cash Advances ${'$'}0.00
         Balance Transfers ${'$'}0.00
         Fees Charged ${'$'}0.00
         Interest Charged ${'$'}0.00
         Opening/Closing Date 08/06/22 - 09/05/22
         Credit Limit ${'$'}4,500
         Available Credit ${'$'}4,476
         Cash Access Line ${'$'}225
         Available for Cash ${'$'}225
         Previous points balance 861
         + 1% (1 Pt)/${'$'}1 earned on all purchases 24
         Start redeeming today. Visit Ultimate Rewards® at
         www.ultimaterewards.com     
         You always earn unlimited 1% cash back on all your purchases.
         Activate new bonus categories every quarter. You'll earn an
         additional 4% cash back, for a total of 5% cash back on up to
         ${'$'}1,500 in combined bonus category purchases each quarter.
         Activate for free at chase.com/freedom, visit a Chase branch or
         call the number on the back of your card.    
         _________________________.___________ Amount Enclosed
         SCENARIO-4D
         JOHN A DOE
         123 TEST ST
         CITY ST 11111-2222
         Payment Due Date
         New Balance
         Minimum Payment Due
         October  2022
         S M T W T F S
         25 26 27 28 29 30 1
         2 3 4 5 6 7 8
         9 10 11 12 13 14 15
         16 17 18 19 20 21 22
         23 24 25 26 27 28 29
         30 31 1 2 3 4 5
         CARDMEMBER SERVICE
         PO BOX 6294
         CAROL STREAM IL 60197-6294
         See Your Account
         Messages for details.
         Your next AutoPay payment for ${'$'}23.75 will be deducted from your Pay From account and credited on your due date.  
         If your due date falls on a Saturday, we'll credit your payment the Friday before.
         Your AutoPay amount will be reduced by any payments or merchant credits that post to your account before we 
         process your  AutoPay payment.  If the total of these payments and merchant credits is more than your set  AutoPay 
         amount, your AutoPay payment for that month will be zero.
         Fraud and scams can happen to anyone.
         Protect yourself and older loved ones by learning the warning signs and other helpful tips. For more information, visit 
         www.chase.com/financialabuse
         111111111111111111111111111111111111111111111
         CHASE FREEDOM: ULTIMATE
         REWARDS® SUMMARY
         Total points available for
         redemption 885
         10/02/22
         ${'$'}23.75
         ${'$'}23.75
         Late Payment Warning:
         Remit Coupon for
         1111111111111111111111111111
         To contact us regarding your account:
         Mail Payments to:
         P.O. Box 6294
         Carol Stream, IL 60197-6294
         Send Inquiries to:
         P.O. Box 15298
         Wilmington, DE 19850-5298
         To manage your account, including card payments, alerts, and change of address, visit 
          or call the customer service number which appears on your
         account statement.
         www.chase.com/cardhelp
         Visit Our Website:
         Call Customer Service:
         www.chase.com/cardhelp
         In U.S. 1-800-524-3880
         Spanish 1-888-446-3308
         Pay by phone1-800-436-7958
         International   1-302-594-8200
         We accept operator relay calls
         Manage your account online at: Mobile:  Download the
         Chase Mobile app today® 
         ACCOUNT ACTIVITY
         Year-to-date totals do not reflect any fee or interest refunds
         you may have received.
         INTEREST CHARGES
         0000001 12312312 D 9 Y 9 05 22/09/05 Page 2 of 2 02222 MA MA 22222 111111111111111111111111 
         0000001 12312312 D 9 Y 9 05 22/09/05 Page 2 of 2 02222 MA MA 22222 111111111111111111111111 
          Customer Service:
         www.chase.com/cardhelp 1-800-524-3880
         2022  Totals Year-to-Date
         Annual Percentage Rate (APR) 
         Balance Type
         Annual
         Percentage
         Rate (APR)
         Balance
         Subject To
         Interest Rate
         Interest
         Charges
         PURCHASES
         CASH ADVANCES
         BALANCE TRANSFERS
         31 Days in Billing Period
         Page 2 of 2 Statement Date: 09/05/22JOHN A DOE
         Date of
         Transaction Merchant  Name or Transaction Description ${'$'} Amount
         08/10     STORE 111-222-3333 SS 23.75
         Total fees charged in 2022 ${'$'}0.00
         Total interest charged in 2022 ${'$'}0.00
         Your  is the annual interest rate on your account.
         Purchases 17.24%(v)(d) - 0 -   - 0 -   
         Cash Advances 27.24%(v)(d) - 0 -   - 0 -   
         Balance Transfer 17.24%(v)(d) - 0 -   - 0 -   
         (v) = Variable Rate
         (d) = Daily Balance Method (including new transactions)
         (a) = Average Daily Balance Method (including new transactions)
         Please see Information About Your Account section for the Calculation of Balance Subject to Interest Rate, Annual Renewal Notice,
         How to Avoid Interest on Purchases, and other important information, as applicable.
         PURCHASE
         Table Summary
         Table Summary
         empty cell empty cell empty cell
         empty cell empty cell empty cell
         empty cell empty cell empty cell
     """.trimIndent()

    @Test
    fun parse() {
        val csvFile = Files.createTempFile(null, null).toFile().apply {
            deleteOnExit()
        }

        StringParserInput(sampleText).use { parserInput ->
            CSVParserOutput(csvFile).use { parserOutput ->
                ChaseStatementParser.parse(parserInput, parserOutput)
            }
        }

        val lines = csvFile.inputStream().use { inputStream ->
            CSVReaderBuilder(inputStream.bufferedReader())
                .withCSVParser(CSVParserBuilder().withSeparator('|').build())
                .build().use { reader ->
                    reader.readAll()
                }
        }

        assertEquals(1, lines.size)
        val line = lines.single()
        assertEquals(4, line.size)
        assertEquals("1234 5678 9012 3456", line[0])
        assertEquals("2022-08-10", line[1])
        assertEquals("STORE 111-222-3333 SS", line[2])
        assertEquals("23.75", line[3])
    }
}