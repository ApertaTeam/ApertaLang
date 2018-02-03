package com.apertasoftware.apertalang;

import java.util.HashMap;
import java.util.Map;

public class ApertaInstance {
    private ApertaClass klass;
    public final Map<String, Object> fields = new HashMap<>();

    ApertaInstance(ApertaClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        ApertaFunction method = klass.findMethod(this, name.lexeme);
        if (method != null) return method;

        Object callable = klass.get(name);
        if(callable != null) return klass.get(name);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
