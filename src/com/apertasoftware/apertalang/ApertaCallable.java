package com.apertasoftware.apertalang;

import java.util.List;

/**
 * Where all the functions and classes inherit from.
 */
interface ApertaCallable {
    /**
     * Function that gets the amount of arguments in the callable.
     * @return - The number of arguments in the callable.
     */
    int arity();

    /**
     * Function that actually executes the callable.
     * @param interpreter - Self explanatory.
     * @param arguments - List of arguments needed by the callable.
     * @return - Whatever the callable returns lol.
     */
    Object call(Interpreter interpreter, List<Object> arguments);
}
