package lexical.impl

import commons.SymbolTable
import lexical.LexicalRecognizer
import lexical.model.State
import lexical.model.StateTable
import lexical.model.Token
import utils.getLogger
import utils.readCsvFile
import kotlin.system.exitProcess


class TableLexicalRecognizer(
    private val tablePath: String,
    private val symbolTable: SymbolTable,
    private var code: String
) : LexicalRecognizer {
    private val logger = getLogger(javaClass)

    private var pos: Int = 0
    private var currentLine: Int = 0
    private var currentColumn: Int = 0
    private val stateTable: StateTable = StateTable()

    init {
        code += ';'
        loadTable(tablePath)
    }

    override fun getToken(): Token? {
        var character: Char? = code.getOrNull(pos) ?: return null
        var value = ""
        var stateInt = 0
        while (!isFinal(stateInt) && character != null) {
            value += character
            stateInt = move(stateInt, character)
            if (stateInt == -1) stop()
            character = code.getOrNull(++pos)
            if (stateTable.states[stateInt].restart) {
                value = ""
                stateInt = 0
            }
            if (character == '\n') {
                currentLine++
                currentColumn = 0
            } else {
                currentColumn++
            }
        }
        val state = stateTable.states[stateInt]
        if (!state.isFinal) return null // Se o estado não é final o while saiu por EOF
        if (state.lookAhead) {
            pos--
            currentColumn--
            value = value.dropLast(1)
            if (currentColumn == 0) currentLine--
        }

        val t = Token(state.code, if (state.returnValue) value else null, currentLine, currentColumn)
        takeIfSymbol(t)
        return t
    }

    private fun takeIfSymbol(t: Token) {
        t.value?.let { symbolTable.putIfAbsent(t.value, Unit) }
    }

    private fun stop() {
        println("Error")
        exitProcess(0)
    }

    private fun move(state: Int, character: Char): Int {
        logger.debug("State:${state}\t Reading: '$character'")
        return stateTable.stateTransitions[state]!![character.toString()]
            ?: stateTable.stateTransitions[state]!!.filterKeys { k -> Regex(k).matches(character.toString()) }.values.firstOrNull()
            ?: -1
    }

    private fun isFinal(state: Int): Boolean {
        return stateTable.states[state].isFinal
    }

    private fun loadTable(path: String) {
        val rows = readCsvFile(path)
        val symbols = rows.removeAt(0).drop(6) // Drop categorias
        logger.debug("Lexical Symbols: $symbols")
        rows.forEach { stateInfo ->
            val state = State(
                stateInfo[0].toInt(),
                stateInfo[1],
                stateInfo[2].toBoolean(),
                stateInfo[3].toBoolean(),
                stateInfo[4].toBoolean(),
                stateInfo[5].toBoolean()
            )
            val tMap = stateInfo.subList(6, stateInfo.size).mapIndexed { index, s ->
                symbols[index] to s.toInt()
            }
                .associateTo(LinkedHashMap()) { it }
            logger.debug("Lexical Transition: ${state.code} -> $tMap")
            stateTable.states += state
            stateTable.stateTransitions[stateTable.states.size - 1] = tMap
        }
    }
}