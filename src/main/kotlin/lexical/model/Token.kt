package lexical.model

data class Token(val name: String, val value: String?, val line: Int, val column: Int)