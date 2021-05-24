package syntatic

import utils.TreeNode

interface SyntaxRecognizer {
    /**
     * True: Cadeia aceita
     * False: Cadeia Rejeitada
     */
    fun run(): Boolean
    fun retrieveTree(): TreeNode<String>
}