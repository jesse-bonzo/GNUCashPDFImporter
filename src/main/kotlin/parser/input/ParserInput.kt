package parser.input

sealed interface ParserInput : AutoCloseable {
    fun getText(): String

}