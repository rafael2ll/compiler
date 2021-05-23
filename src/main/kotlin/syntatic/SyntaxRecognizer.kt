package syntatic

interface SyntaxRecognizer {
    /**
     * True: Cadeia aceita
     * False: Cadeia Rejeitada
     */
    fun run(): Boolean
}