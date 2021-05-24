import commons.SymbolTable
import lexical.LexicalRecognizer
import lexical.impl.TableLexicalRecognizer
import syntatic.impl.TableSyntaxRecognizer
import utils.getLogger

class Program

val logger = getLogger(Program::class.java)

fun main(args: Array<String>) {
    val symbolTable = SymbolTable()
    var i = 0
    val code = Program::class.java.getResource("/sample.code").readText(Charsets.UTF_8)
    val lexicalRecognizer: LexicalRecognizer = TableLexicalRecognizer("/grammar.csv", symbolTable, code)
    val syntaxRecognizer = TableSyntaxRecognizer("/tabela_sintatica.csv", "/producoes_sintatica.csv", lexicalRecognizer)

    logger.debug("Codigo: $code")
    if (syntaxRecognizer.run()) {
        println("Programa aceito")
        println(syntaxRecognizer.retrieveTree())
    } else
        println("Programa Rejeitado")
}