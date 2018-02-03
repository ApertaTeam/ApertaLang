package com.apertasoftware.apertalang;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    /**
     * The Token object used by nearly all parts of the interpreter.
     * @param type - The type of Token being created.
     * @param lexeme - The actual character(s) used to create the token.
     * @param literal - What the token actually resolves to, can only be a Number, Boolean, Null, or String.
     * @param line - The line the token was defined on in the Source Code.
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
