package hso.jsimple;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.*;

import hso.JSimple;
import hso.TodoException;

record JSimpleCmdlineArgs(boolean todoOk, String srcFile) {}
record JSimpleTestResult(int ok, int fail, int total, boolean foundTodo) {}

public class JSimpleTestRunner {

    private static void abort(String msg) {
        System.err.println(msg);
        System.err.println("Aborting");
        System.exit(1);
    }
    
    private static JSimpleCmdlineArgs parse(String[] args) {
        boolean todoOk = false;
        boolean help = false;
        String srcFile = null;
        for (String s : args) {
            switch (s) {
                case "--help" -> {
                    help = true;
                }
                case "--todo-ok" -> {
                    todoOk = true;
                }
                default -> {
                    if (s.startsWith("--")) {
                        abort("Invalid option: " + s);
                    }
                    if (srcFile != null) {
                        abort("Multiple source files given");
                    }
                    srcFile = s;
                }
            }
        }
        if (srcFile == null) {
            abort("No source file given");
        }
        if (help) {
            System.out.println("USAGE: java hso.jsimple.JSimpleTestRunner [OPTIONS] FILE.java");
            System.out.println("");
            System.out.println("Runs the main methods of all classes in FILE.java");
            System.out.println("and reports on success/failure of all JSimple.check calls.");
            System.out.println("");
            System.out.println("OPTIONS");
            System.out.println("  --todo-ok  It's ok if a check aborts with TodoException");
            System.out.println("  --help     Print this message");
            System.exit(0);
        }
        return new JSimpleCmdlineArgs(todoOk, srcFile);
    }
    
    private static String getNameFromMatch(Pattern pat, String input) {
        var m = pat.matcher(input);
        if (m.matches()) {
            return m.group("Name");
        } else {
            return null;
        }
    }
    private static List<String> findClasses(String srcFile) {
        Pattern pkgPattern = Pattern.compile("^package\\s+(?<Name>[a-zA-Z0-9_.]+)\\s*;\\s*");
        Pattern classPattern = Pattern.compile("^(public\\s+)?(class|record)\\s+(?<Name>[a-zA-Z0-9_]+).*");
        String pattern = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(srcFile))) {
            List<String> result = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (pattern == null) {
                    pattern = getNameFromMatch(pkgPattern, line);
                }
                String cls = getNameFromMatch(classPattern, line);
                if (cls != null) {
                    String fullName = cls;
                    if (pattern != null) {
                        fullName = pattern + "." + cls;
                    }
                    result.add(fullName);
                }
            }
            if (result.size() == 0) {
                abort("No classes or records found in " + srcFile);
            }
            return result;
        } catch (IOException e) {
            abort("Error reading content of " + srcFile + ": " + e);
            return null;
        }
    }
    
    // Returns true if an  TodoException is thrown by the test
    private static boolean runTests(String clsName) {
        Class<?> cls = null;
        try {
            cls = Class.forName(clsName);
        } catch(ClassNotFoundException e) {
            abort("Could not load class " + clsName);
        }
        Method meth = null;
        try {
            meth = cls.getMethod("main", String[].class);
        } catch(NoSuchMethodException e) {
            return false; // that's ok
        }
        meth.setAccessible(true);
        String[] params = new String[0];
        try {
            meth.invoke(null, (Object) params);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TodoException todoExc) {
                return true;               
            } else {
                abort("main-method of C2 failed with exception " + 
                        cause.getClass().getName() + ": " + cause);
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            abort("Could not invoke main-method of " + clsName + ": " + e);
        }
        return false;
    }
    
    static JSimpleTestResult runAllTests(JSimpleCmdlineArgs args) {
        List<String> classes = findClasses(args.srcFile());
        boolean foundTodo = false;
        for (String clsName : classes) {
            if (runTests(clsName)) {
                foundTodo = true;
            }
        }
        int fail = JSimple.getFailTestCount();
        int ok = JSimple.getOkTestCount(); 
        return new JSimpleTestResult(ok, fail, ok + fail, foundTodo);
    }
    
    public static void main(String[] argArray) {
        JSimpleCmdlineArgs args = parse(argArray);
        JSimpleTestResult result = runAllTests(args);
        System.out.println(result);
        if (result.foundTodo()) {
            if (!args.todoOk()) {
                abort("Found unexpected TodoException");
            }
        }
        if (result.total() == 0) {
            if (!result.foundTodo()) {
                System.out.println("No tests found!");
            }
        } else if (result.fail() == 0) {
            System.out.println("All tests ok");
            System.exit(0);
        } else {
            System.out.println(result.fail() + " of " + result.total() + " tests failed!");
            System.exit(1);
        }
    }

}
