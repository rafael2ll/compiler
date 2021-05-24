package syntatic.impl

import lexical.LexicalRecognizer
import syntatic.SyntaxRecognizer
import syntatic.model.Production
import syntatic.model.Symbol
import syntatic.model.Table
import utils.TreeNode
import utils.getLogger
import utils.readCsvFile
import kotlin.system.exitProcess

class TableSyntaxRecognizer(
    private val tablePath: String,
    private val productionPath: String,
    private val lexicalRecognizer: LexicalRecognizer
) : SyntaxRecognizer {
    private val logger = getLogger(javaClass)

    private val stack = ArrayDeque<Symbol>()
    private val table = Table()
    private val productionSet = hashMapOf<Int, Production>()
    private var tree: TreeNode<String>

    init {
        loadProductions()
        loadTable()
        val initialSymbol = productionSet[1]!!.first()
        stack.addLast(initialSymbol)
        tree = TreeNode(initialSymbol.head!!)
        tree.addChild(TreeNode(stack.last().value))
        table.keys.forEach {
            logger.debug("Syntax Table: $it -> ${table[it]}")
        }
    }

    override fun run(): Boolean {
        var proxToken = lexicalRecognizer.getToken()
        var subTree = tree.nextChild()
        while (stack.isNotEmpty()) {
            val X = stack.last()
            logger.debug("All Stack: ${stack.map { it.value }}")
            logger.debug("Top Stack: $X")
            if (X.isTerminal) {
                if (X.value.uppercase() == proxToken!!.name.uppercase()) {
                    stack.removeLast()
                    proxToken = lexicalRecognizer.getToken()
                    subTree = subTree.parent!!.nextChild()
                } else {
                    println("${X.value} esperado!")
                    exitProcess(0)
                }
            } else if (X.empty) {
                stack.removeLast()
                subTree = subTree.parent!!.nextChild()
            } else {
                val derivation = table[X.value]?.get(proxToken?.name?.lowercase() ?: "$")
                if (derivation == null) {
                    println("Erro[${X.value}][${proxToken!!.name.lowercase()}]: $derivation")
                    exitProcess(0)
                } else {
                    // TODO: Trata Producao
                    stack.removeLast()
                    productionSet[derivation]!!.asReversed().forEach { symbol ->
                        stack.addLast(symbol)
                    }
                    productionSet[derivation]!!.forEach { symbol ->
                        subTree.addChild(TreeNode(symbol.value))
                    }
                    subTree = subTree.nextChild()
                }
            }
        }
        if (proxToken != null) println("${proxToken.name} não esperado")
        return proxToken == null
    }

    override fun retrieveTree(): TreeNode<String> {
        return tree
    }

    private fun loadProductions() {
        val rows = readCsvFile(productionPath)
        rows.removeAt(0) // Remove header line
        rows.forEach { cols ->
            logger.debug("Gramatica: $cols")
            val productionPos = cols.drop(0)[0].toInt()
            val values = cols[1].split("->")
            val head = values[0].trim()
            val tokens: Production = values[1].trim().split(" ")
                .map { symbol ->
                    val empty = symbol == "_"
                    Symbol(head, symbol, symbol.all { it.isLowerCase() || !it.isLetterOrDigit() } && !empty, empty)
                }
            productionSet[productionPos] = tokens
        }
    }

    private fun loadTable() {
        val rows = readCsvFile(tablePath)
        val symbols = rows.removeAt(0).drop(1)
        logger.debug("Terminals: $symbols")
        rows.forEach { cols ->
            val symbol = cols.removeAt(0)
            logger.debug("Parsing table: Symbol $symbol -> $cols")
            val symbolMap = cols.mapIndexed { index, s -> symbols[index] to s.toIntOrNull() }
                .associateTo(HashMap()) { it }
            table[symbol] = symbolMap
        }
    }
}