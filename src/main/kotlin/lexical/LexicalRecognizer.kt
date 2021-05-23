package lexical

import lexical.model.Token

interface LexicalRecognizer {
    fun getToken(): Token?
}