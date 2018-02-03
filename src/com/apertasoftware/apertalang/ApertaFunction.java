package com.apertasoftware.apertalang;

import java.util.List;

class ApertaFunction implements ApertaCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    ApertaFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    ApertaFunction bind(ApertaInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new ApertaFunction(declaration, environment, isInitializer);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if(declaration.returnType.type == TokenType.VOID) return null;
            if(declaration.returnType.type == TokenType.ANY) return returnValue.value;
            if((returnValue.value instanceof Double && declaration.returnType.type != TokenType.NUMBER) || (returnValue.value instanceof String && declaration.returnType.type != TokenType.STRING)) {
                throw new RuntimeError(declaration.name, "Return type doesn't match return value.");
            }
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
