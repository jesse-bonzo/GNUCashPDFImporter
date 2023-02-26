package parser

import mu.KotlinLogging
import pdf.extractText
import java.io.File

object StatementTypeDiscoverer {

    private val log = KotlinLogging.logger { }

    fun discover(inputFile: File): String {
        val pdfText = extractText(inputFile)

        if (pdfText.contains("americanexpress.com") && pdfText.contains("1-888-258-3741")) {
            log.info { "Statement is from American Express" }
            return "amex"
        }

        if (pdfText.contains("(646) 600-8263") && pdfText.contains("support@betterment.com")) {
            log.info { "Statement is from Betterment" }
            return "betterment"
        }

        if (pdfText.contains("www.bankofamerica.com") && pdfText.contains("1.800.421.2110")) {
            log.info { "Statement is from Bank of America" }
            return "bofa"
        }

        if (pdfText.contains("capitalone.com") && pdfText.contains("1-888-464-0727") && pdfText.contains("P.O. Box 60, St. Cloud, MN 56302")) {
            log.info { "Statement is from Capital One" }
            return "capitalone"
        }

        if (pdfText.contains("www.chase.com/cardhelp") && pdfText.contains("1-800-524-3880")) {
            log.info { "Statement is from Chase" }
            return "chase"
        }

        if (pdfText.contains("www.citicards.com") && pdfText.contains("1-855-473-4583")) {
            log.info { "Statement is from Citi" }
            return "citi"
        }

        if (pdfText.contains("© 2014 Discover Bank, Member FDIC.")) {
            log.info { "Statement is from Discover" }
            return "discover"
        }

        if (pdfText.contains("402-938-3614") && pdfText.contains("PayPal account statement")) {
            log.info { "Statement is from PayPal" }
            return "paypal"
        }

        if (pdfText.contains("support@robinhood.com") && pdfText.contains("85 Willow Rd, Menlo Park, CA 94025")) {
            log.info { "Statement is from Robinhood" }
            return "robinhood"
        }

        if (pdfText.contains("vanguard.com") && pdfText.contains("800-662-2739")) {
            log.info { "Statement is from Vanguard" }
            return "vanguard"
        }

        throw Exception("Unable to determine statement type. Please specify it.")
    }
}