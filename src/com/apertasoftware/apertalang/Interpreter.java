package com.apertasoftware.apertalang;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The actual interpreter, which as you can see, implements the abstract Expr and Stmt classes.
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {

        new Globals(this);

    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            ApertaLang.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        if(expr.value instanceof List<?>) {
            List<Object> arrayValue = new ArrayList<>();
            for (Expr val : (List<Expr>)expr.value) {
                arrayValue.add(evaluate(val));
            }
            return arrayValue;
        } else {
            return expr.value;
        }
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);

        if (object instanceof List<?>) {
            ((List<Expr>) object).add(((Double)expr.name.literal).intValue(), expr.value);
            return evaluate(expr.value);
        }

        if (!(object instanceof ApertaInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((ApertaInstance)object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        ApertaClass superclass = (ApertaClass)environment.getAt(distance, "super");

        ApertaInstance object = (ApertaInstance)environment.getAt(distance - 1, "this");

        ApertaFunction method = superclass.findMethod(object, expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }

        return method;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        return null;
    }

    @Override
    public Object visitLambdaExpr(Expr.Lambda expr) {
        return new ApertaLambda(expr, environment);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void reference(String file) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        String source = new String(bytes, Charset.defaultCharset());
        Scanner preprocScan = new Scanner(source);
        Parser preprocParse = new Parser(preprocScan.scanTokens());
        List<Stmt> preprocStatements = preprocParse.parse();
        Resolver preprocResolver = new Resolver(this);
        preprocResolver.resolve(preprocStatements);
        this.interpret(preprocStatements);
    }

    private void include(String file) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        String source = new String(bytes, Charset.defaultCharset());
        Scanner preprocScan = new Scanner(source);
        Parser preprocParse = new Parser(preprocScan.scanTokens());
        List<Stmt> preprocStatements = preprocParse.parse();
        Resolver preprocResolver = new Resolver(this);
        preprocResolver.resolve(preprocStatements);
        this.interpret(preprocStatements);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            if(operator.type == TokenType.SLASH) {
                if((double)right == 0.0) throw new RuntimeError(operator, "Can't divide by zero!");
            } else {
                return;
            }
        }

        throw new RuntimeError(operator, "Operands must be numbers");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Double) return (Double)object != 0;
        if (object instanceof String) return !((String)object).equals("");
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.
        if(a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    public String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        if (object instanceof List<?>) {
            StringBuilder text = new StringBuilder();
            for (Expr val : (List<Expr>)object) {
                text.append(", ").append(stringify(evaluate(val)));
            }
            if(text.length() == 0) return "[]";
            return "[" + text.toString().substring(2) + "]";
        }

        return object.toString();
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        environment.define(stmt.name.lexeme, null);
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof ApertaClass)) {
                throw new RuntimeError(stmt.name, "Superclass must be a class.");
            }
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, ApertaFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            ApertaFunction function = new ApertaFunction(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        ApertaClass klass = new ApertaClass(stmt.name.lexeme, (ApertaClass)superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        ApertaFunction function = new ApertaFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPreprocStmt(Stmt.Preproc stmt) {
        try {
            switch (stmt.type.lexeme) {
                case "reference":
                    reference((String)stmt.value.literal);
                    break;
                case "include":
                    include((String)stmt.value.literal);
                    break;
                default:
                    throw new RuntimeError(stmt.type, "Invalid preprocessor type '" + stmt.type.lexeme + "'.");
            }
        } catch (IOException e) {
            throw new RuntimeError(stmt.value, e.getMessage());
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Object visitArrayExpr(Expr.Array expr) {
        List<Object> array = new ArrayList<>();
        for (Expr val : expr.objects) {
            array.add(evaluate(val));
        }
        return array;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                if (left instanceof String) {
                    return (String)left + stringify(right);
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if(!(callee instanceof ApertaCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        ApertaCallable function = (ApertaCallable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }

        try {
            Object val = function.call(this, arguments);
            return val;
        } catch (Error e) {
            throw new RuntimeError(expr.paren, e.getMessage());
        }
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof ApertaInstance) {
            return ((ApertaInstance) object).get(expr.name);
        } else if (object instanceof ApertaClass) {
            return ((ApertaClass) object).get(expr.name);
        } else if (object instanceof List<?>) {
            if(((List<Expr>)object).size()-1 < ((Double)expr.name.literal).intValue()) {
                return null;
            }
            return evaluate(((List<Expr>) object).get(((Double)expr.name.literal).intValue()));
        }

        throw new RuntimeError(expr.name, "Only instances and classes have properties.");
    }
}
