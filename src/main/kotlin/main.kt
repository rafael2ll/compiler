import lexical.LexicalRecognizer
import lexical.impl.TableLexicalRecognizer
import commons.SymbolTable

fun main(args: Array<String>) {
    println("Hello World!")
    val symbolTable = SymbolTable()
    var i = 0
    val lexicalRecognizer: LexicalRecognizer = TableLexicalRecognizer(
        symbolTable, "" +
                "faca(a=b)\n" +
                "inicio\n" +
                "print(a+b)\n" +
                "fim\n" +
                "int a = a * b + c\n" +
                "float b = a/b\n" +
                "string k= \"Avestruz de pijama\"" +
                "se(a>b) entao print(c)senao enquanto(b > a) inicio print(b) fim"
    )
    do {
        val token = lexicalRecognizer.getToken()
        println(token)
    } while (token != null)

    print(symbolTable)
}