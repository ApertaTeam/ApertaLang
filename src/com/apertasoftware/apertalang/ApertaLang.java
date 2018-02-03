package com.apertasoftware.apertalang;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ApertaLang {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: Japt [file]");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Interprets source code from a file.
     * @param path - The file to be interpreted.
     * @throws IOException - Using File throws an IOException, mainly if the file doesn't exist.
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    /**
     * Starts the interpreter by itself.
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.println("> ");
            run(reader.readLine());
            hadError = false;
        }
    }

    /**
     * The actual interpreter process used by both runFile and runPrompt.
     * @param source - The source code to be run.
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there's a parsing error.
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there's a resolution error.
        if (hadError) return;

        interpreter.interpret(statements);
    }

    /**
     * An alternate overload to the Report function.
     * @param line - Line in the source code where the error happened.
     * @param message - The message to print.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * A function used by the parser and interpreter to throw compile errors.
     * @param line - Line in the source code where the error happened.
     * @param where - Where specifically the error happened in the line.
     * @param message - The message to print.
     */
    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * An alternate overload to the Report function taking a token instead.
     * @param token - The last token to be consumed when the error happened.
     * @param message - The message to print.
     */
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * A function to warn the user of bad coding practices in the source code.
     * @param token - The last token to be consumed when the warning was triggered.
     * @param message - The warning to print.
     */
    static void warn(Token token, String message) {
        System.out.println(
                "[WARN] " + message + "\n[line " + token.line + "]"
        );
    }

    /**
     * Function called when Runtime Error is caught by the interpreter.
     * @param error - The error caught.
     */
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
