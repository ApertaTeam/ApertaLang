package com.apertasoftware.apertalang;

import jdk.nashorn.internal.objects.NativeFloat32Array;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

import static jdk.nashorn.internal.objects.NativeMath.*;

/**
 * A little shortcut class for all the global classes/functions.
 */
public class Globals {

    Globals(Interpreter interpreter) {
        new GlobalsMath(interpreter);
        new GlobalsDate(interpreter);
        new GlobalsIO(interpreter);
    }

    private class GlobalsMath {
        GlobalsMath(Interpreter interpreter) {
            Map<String, ApertaCallable> math = new HashMap<>();

            math.put("abs", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.abs((Double)arguments.get(0));
                }
            });

            math.put("acos", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.acos((Double)arguments.get(0));
                }
            });

            math.put("acosh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.log((Double)arguments.get(0) + Math.sqrt((Double)arguments.get(0) * (Double)arguments.get(0) - 1));
                }
            });

            math.put("asin", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.asin((Double)arguments.get(0));
                }
            });

            math.put("asinh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Double value = (Double)arguments.get(0);
                    if(Double.isInfinite((Double)arguments.get(0))) {
                        return arguments.get(0);
                    } else {
                        return Math.log(value + Math.sqrt(value * value + 1));
                    }
                }
            });

            math.put("atan", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.atan((Double)arguments.get(0));
                }
            });

            math.put("atanh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Double val = (Double)arguments.get(0);
                    return Math.log((1+val)/(1-val)) / 2;
                }
            });

            math.put("atan2", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.atan2((Double)arguments.get(0), (Double)arguments.get(1));
                }
            });

            math.put("cbrt", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.cbrt((Double)arguments.get(0));
                }
            });

            math.put("ceil", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.ceil((Double)arguments.get(0));
                }
            });

            math.put("clz32", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    if(arguments.get(0) == null || (Double)arguments.get(0) == 0) {
                        return 32;
                    }
                    Double val = (Double)arguments.get(0);
                    return 31 - Math.floor(Math.log(val.intValue() >>> 0) * 1.4426950408889634);
                }
            });

            math.put("cos", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.cos((double)arguments.get(0));
                }
            });

            math.put("cosh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.cosh((double)arguments.get(0));
                }
            });

            math.put("exp", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.exp((double)arguments.get(0));
                }
            });

            math.put("expm1", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.expm1((double)arguments.get(0));
                }
            });

            math.put("floor", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.floor((double)arguments.get(0));
                }
            });

            math.put("fround", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    NativeFloat32Array array = NativeFloat32Array.constructor(true, 1);
                    array.set(0, (double)arguments.get(0), 0);
                    return array.get(0);
                }
            });

            math.put("hypot", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.hypot((double)arguments.get(0), (double)arguments.get(1));
                }
            });

            math.put("imul", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Double a = (double)arguments.get(0);
                    Double b = (double)arguments.get(1);
                    int aHi = (a.intValue() >>> 16) & 0xffff;
                    int aLo = a.intValue() & 0xffff;
                    int bHi = (b.intValue() >>> 16) & 0xffff;
                    int bLo = b.intValue() & 0xffff;
                    return ((aLo * bLo) + (((aHi * bLo + aLo * bHi) << 16) >>> 0) | 0);
                }
            });

            math.put("log", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.log((double)arguments.get(0));
                }
            });

            math.put("log1p", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.log1p((double)arguments.get(0));
                }
            });

            math.put("log10", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.log10((double)arguments.get(0));
                }
            });

            math.put("log2", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.log((double)arguments.get(0)) * LOG2E;
                }
            });

            math.put("max", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.max((double)arguments.get(0), (double)arguments.get(1));
                }
            });

            math.put("min", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.min((double)arguments.get(0), (double)arguments.get(1));
                }
            });

            math.put("pow", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.pow((double)arguments.get(0), (double)arguments.get(1));
                }
            });

            math.put("random", new ApertaCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.random();
                }
            });

            math.put("round", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.round((double)arguments.get(0));
                }
            });

            math.put("sign", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Double x = (double)arguments.get(0);
                    return (boolToInt(x > 0) - boolToInt(x < 0));
                }
            });

            math.put("sin", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.sin((double)arguments.get(0));
                }
            });

            math.put("sinh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.sinh((double)arguments.get(0));
                }
            });

            math.put("sqrt", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.sqrt((double)arguments.get(0));
                }
            });

            math.put("tan", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.tan((double)arguments.get(0));
                }
            });

            math.put("tanh", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return Math.tanh((double)arguments.get(0));
                }
            });

            math.put("toSource", new ApertaCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return "Math";
                }
            });

            math.put("trunc", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return ((Double)arguments.get(0)).intValue();
                }
            });

            ApertaInstance mathInst = new ApertaInstance(new ApertaClass("Math", null, new HashMap<>(), math));
            mathInst.fields.put("E", E);
            mathInst.fields.put("LN2", LN2);
            mathInst.fields.put("LN10", LN10);
            mathInst.fields.put("LOG2E", LOG2E);
            mathInst.fields.put("LOG10E", LOG10E);
            mathInst.fields.put("PI", PI);
            mathInst.fields.put("SQRT1_2", SQRT1_2);
            mathInst.fields.put("SQRT2", SQRT2);

            interpreter.globals.define("Math", mathInst);

            interpreter.globals.define("Infinity", Integer.MAX_VALUE);

            interpreter.globals.define("isNaN", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return !(arguments.get(0) instanceof Double);
                }
            });
        }
    }

    private class GlobalsIO {
        GlobalsIO(Interpreter interpreter) {
            Map<String, ApertaCallable> io = new HashMap<>();

            io.put("read", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    new Thread(() -> {
                        if (!(arguments.get(1) instanceof ApertaCallable)) {
                            throw new Error("Argument 3 must be a Function or Lambda.");
                        }
                        try {
                            String line;
                            StringBuilder returnStr = new StringBuilder();
                            BufferedReader reader = new BufferedReader(new FileReader((String)arguments.get(0)));
                            while((line = reader.readLine()) != null) {
                                returnStr.append(line + "\n");
                            }
                            reader.close();
                            ApertaCallable func = (ApertaCallable) arguments.get(1);
                            func.call(interpreter, Arrays.asList((Object) null,(Object) returnStr.toString().substring(0, returnStr.length()-1)));
                        } catch (IOException e) {
                            ApertaCallable func = (ApertaCallable) arguments.get(1);
                            func.call(interpreter, Arrays.asList((Object)e.getMessage(),(Object) ""));
                        }
                    }).start();
                    return null;
                }
            });

            io.put("readSync", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        String line;
                        StringBuilder returnStr = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader((String)arguments.get(0)));
                        while((line = reader.readLine()) != null) {
                            returnStr.append(line + "\n");
                        }
                        reader.close();
                        return returnStr.toString().substring(0, returnStr.length()-1);
                    } catch (IOException e) {
                        throw new Error(e.getMessage());
                    }
                }
            });

            io.put("writeSync", new ApertaCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter((String)arguments.get(0)));
                        writer.write(interpreter.stringify(arguments.get(1)));
                        writer.close();
                    } catch (IOException e) {
                        return e.getMessage();
                    }
                    return null;
                }
            });

            io.put("write", new ApertaCallable() {
                @Override
                public int arity() {
                    return 3;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    new Thread(() -> {
                        if (!(arguments.get(2) instanceof ApertaCallable)) {
                            throw new Error("Argument 3 must be a Function or Lambda.");
                        }
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter((String)arguments.get(0)));
                            writer.write(interpreter.stringify(arguments.get(1)));
                            writer.close();
                            ApertaCallable func = (ApertaCallable) arguments.get(2);
                            func.call(interpreter, Arrays.asList((Object)null));
                        } catch (IOException e) {
                            ApertaCallable func = (ApertaCallable) arguments.get(2);
                            func.call(interpreter, Arrays.asList((Object)e.getMessage()));
                        }
                    }).start();
                    return null;
                }
            });

            interpreter.globals.define("IO", new ApertaClass("IO", null, new HashMap<>(), io));
        }
    }

    private class GlobalsDate {
        GlobalsDate(Interpreter interpreter) {
            interpreter.globals.define("clock", new ApertaCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return (double)System.currentTimeMillis() / 1000.0;
                }
            });
            Map<String, ApertaCallable> methods = new HashMap<>();
            methods.put("now", new ApertaCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return new Date();
                }
            });
            methods.put("toString", new ApertaCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return DateFormat.getDateInstance().format(arguments.get(0));
                }
            });
            ApertaClass Date = new ApertaClass("Date", null, new HashMap<>(), methods);
            interpreter.globals.define("Date", new ApertaInstance(Date));
        }
    }

    private int boolToInt(boolean b) {
        if(b) {
            return 1;
        } else {
            return 0;
        }
    }
}
