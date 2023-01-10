package parser.input

class StringParserInput(val input: String) : ParserInput {
    override fun getText() = input

    override fun close() {
        // NA
    }
}