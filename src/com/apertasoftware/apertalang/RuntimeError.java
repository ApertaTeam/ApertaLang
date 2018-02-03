package com.apertasoftware.apertalang;

/**
 * A class used by the Interpreter to print out where the error happened and what the reason was without crashing the program.
 */
public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
