package hso;

import java.util.*;

import hso.jsimple.ReflectionHelper;

public class JSimple {

    /* Mathematical functions */
    public static int round(double x) {
        return (int)Math.round(x);
    }
    
    public static int floor(double x) {
        return (int)Math.floor(x);
    }
    
    public static int ceil(double x) {
        return (int)Math.ceil(x);
    }
    
    /* TODOs */
    public static TodoException todo(String msg) {
        return new TodoException(msg);
    }
    
    public static TodoException todo() {
        StackTraceElement entry = getStackTraceElement();
        if (entry == null) {
            return todo("TODO");
        }
        String methodName = entry.getMethodName();
        return new TodoException("TODO in method " + methodName + getFormattedLocation(entry));
    }
    
    public static ImpossibleException impossible(String msg) {
        return new ImpossibleException(msg);
    }
    
    public static ImpossibleException impossible() {
        StackTraceElement entry = getStackTraceElement();
        if (entry == null) {
            return impossible("impossible");
        }
        String methodName = entry.getMethodName();
        return new ImpossibleException("The impossible happened in method " + methodName + getFormattedLocation(entry));
    }
    
    public static IllegalException illegal(String msg) {
        return new IllegalException(msg);
    }
    
    public static IllegalException illegal() {
        StackTraceElement entry = getStackTraceElement();
        if (entry == null) {
            return illegal("illegal");
        }
        String methodName = entry.getMethodName();
        return new IllegalException("Something illegal happened in method " + methodName + getFormattedLocation(entry));
    }    
    
    /* TESTS */
    static {
        Thread hook = new Thread(() -> shutdownHook());
        Runtime.getRuntime().addShutdownHook(hook);
    }
	
	private static int okCount = 0;
	private static int failCount = 0;
    
	private static double EPSILON = 0.0000001;
	
	private static void testOk(String msg) {
	    okCount++;
        System.out.flush();
	    System.out.println("[TEST] OK:   " + msg + getFormattedLocation());
        System.out.flush();
	}
	
	private static boolean ignoreClassForLocation(String s) {
        String thisClassName = JSimple.class.getName();
        return (s == null) || s.equals(thisClassName) || s.startsWith("java.");
	}
	
	private static StackTraceElement getStackTraceElement() {
        StackTraceElement[] entries = Thread.currentThread().getStackTrace();
        for (StackTraceElement entry : entries) {
            String className = entry.getClassName();
            if (ignoreClassForLocation(className)) {
                continue;
            }
            return entry;
            
        }
        return null;
    }
	
	private static String getLocation(StackTraceElement entry) {
        if (entry == null) {
            return null;
        }
        String file = entry.getFileName();
        int line = entry.getLineNumber();
        return file + ":" + line;	    
	}

	private static String getFormattedLocation(StackTraceElement entry) {
        String s = getLocation(entry);
        if (s == null) {
            return "";
        } else {
            return " (" + s + ")";
        }	    
	}
	
	private static String getFormattedLocation() {
	    StackTraceElement entry = getStackTraceElement();
        return getFormattedLocation(entry);
	}
	
	private static void testFail(String msg) {
	    failCount++;
        System.out.flush();
        System.out.println("[TEST] FAIL: " + msg + getFormattedLocation());
        System.out.flush();
    }
	
	public static int getFailTestCount() {
	    return failCount;
	}
	
   public static int getOkTestCount() {
        return okCount;
    }
   
   // for tests
   public static void resetTestCounts() {
       failCount = 0;
       okCount = 0;
   }

	private static void shutdownHook() {
        System.out.flush();
        int total = okCount + failCount;
        if (total == 0) {
            return;
        } else {
            if (failCount == 0) {
                System.out.println("[TEST] " + total + " tests, all ok :-)");
            } else {
                System.out.println("[TEST] " + total + " tests, " + failCount + " failed :-( ");
            }
        }
	}
		
	private static String toString(Object x) {
	    if (x instanceof String s) {
	        return "\"" + s + "\"";
	    } else {
	        return ReflectionHelper.genericToString(x);
	    }
	}
	
    public static void check(Object given, Object expected) {
        boolean eq = ReflectionHelper.genericEquality(given, expected);
        String givenStr = toString(given);
        if (!eq) {
            String expectedStr = toString(expected);
            if (givenStr.length() + expectedStr.length() <= 60) {
                testFail("Given " + givenStr + " but expected " + expectedStr + ".");
            } else {
                testFail("Given\n" + givenStr + "\nbut expected\n" + expectedStr);
            }
        } else {
            testOk("Given " + givenStr + " as expected.");
        }
	}	
	
	/* AUXILIARIES for Lists */
	@SafeVarargs
    public static <T> java.util.List<T> makeList(T... elems) {
	    ArrayList<T> result = new ArrayList<T>();
	    for (T x : elems) {
	        result.add(x);
	    }
	    return result;
	}
}
