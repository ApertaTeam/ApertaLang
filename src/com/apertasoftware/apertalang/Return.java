package com.apertasoftware.apertalang;

/**
 * Extends RuntimeException so any return statement used in a function can be unwinded to the original scope.
 */
class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
