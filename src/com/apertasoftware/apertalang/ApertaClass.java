package com.apertasoftware.apertalang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ApertaClass implements ApertaCallable {
    final String name;
    final ApertaClass superclass;
    private final Map<String, ApertaFunction> methods;
    private final Map<String, ApertaCallable> $methods;

    ApertaClass(String name, ApertaClass superclass, Map<String, ApertaFunction> methods) {
        this(name, superclass, methods, new HashMap<>());
    }

    ApertaClass(String name, ApertaClass superclass, Map<String, ApertaFunction> methods, Map<String, ApertaCallable> $methods) {
        this.superclass = superclass;
        this.name = name;
        this.methods = methods;
        this.$methods = $methods;
    }

    ApertaFunction findMethod(ApertaInstance instance, String name) {
        if (methods.containsKey(name)) {
            return methods.get(name).bind(instance);
        }

        if (superclass != null) {
            return superclass.findMethod(instance, name);
        }

        return null;
    }

    Object get(Token name) {
        if(methods.containsKey(name.lexeme)) {
            return methods.get(name.lexeme);
        }

        if($methods.containsKey(name.lexeme)) {
            return $methods.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        ApertaInstance instance = new ApertaInstance(this);
        ApertaFunction initializer = methods.get("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        } else {
            ApertaCallable anonInitializer = $methods.get("init");
            if (anonInitializer != null) {
                anonInitializer.call(interpreter, arguments);
            }
        }
        return instance;
    }

    @Override
    public int arity() {
        ApertaFunction initializer = methods.get("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
