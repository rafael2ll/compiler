package lexical.model

data class State(
    val state: Int,
    val code: String,
    val isFinal: Boolean,
    val restart: Boolean,
    val lookAhead: Boolean,
    val returnValue: Boolean
)
