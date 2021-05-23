package syntatic.impl

import lexical.LexicalRecognizer
import syntatic.SyntaxRecognizer
import syntatic.model.Symbol
import syntatic.model.Table
import kotlin.system.exitProcess

class TableSyntaxRecognizer(private val lexicalRecognizer: LexicalRecognizer) : SyntaxRecognizer {
    private val stack = ArrayDeque<Symbol>()
    private val table = Table()

    init {
        table["S"]!!["S"]!!.let { stack.addLast(it[0]) }
    }

    override fun run(): Boolean {
        var proxToken = lexicalRecognizer.getToken()
        while (stack.isNotEmpty()) {
            val X = stack.last()
            if (X.isTerminal) {
                if (X.value == proxToken!!.name) {
                    stack.removeLast()
                    proxToken = lexicalRecognizer.getToken()
                } else {
                    println("${X.value} esperado!")
                    exitProcess(0)
                }
            } else {
                val derivation = table[X.value]?.get(proxToken!!.name)
                if (derivation == null) {
                    println("Erro!!")
                    exitProcess(0)
                } else {
                    // TODO: Trata Producao
                    stack.removeLast()
                    derivation.asReversed().forEach { symbol -> stack.addLast(symbol) }
                }
            }
        }
        if (proxToken != null) println("${proxToken.name} não esperado")
        return proxToken == null
    }
}