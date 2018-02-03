package com.apertasoftware.apertalang;

import java.util.List;

public class ApertaLambda implements ApertaCallable {
    private final Expr.Lambda declaration;
    private final Environment closure;

    ApertaLambda(Expr.Lambda declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).lexeme, arguments.get(i));
        }
        interpreter.executeBlock(declaration.body, environment);
        return null;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        returnString.append("<lambda (");
        boolean first = true;
        for(Token parameter : declaration.parameters) {
            if(first) {
                returnString.append(parameter.lexeme);
                first = false;
            } else {
                returnString.append(", " + parameter.lexeme);
            }
        }
        returnString.append(")>");
        return returnString.toString();
    }
}
