package parser

import parser.input.ParserInput
import parser.output.ParserOutput

sealed interface StatementParser {
    fun parse(input: ParserInput, output: ParserOutput)
}