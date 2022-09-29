package hso;

import java.util.*;

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
    
	public static void check(double given, double expected) {
		if (Math.abs(given - expected) >= EPSILON) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + expected + " as expected.");
		}
	}
	
	public static void check(float given, float expected) {
		if (Math.abs(given - expected) >= EPSILON) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + expected + " as expected.");
		}
	}
	
	public static void check(long given, long expected) {
		if (given != expected) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + given + " as expected.");
		}
	}	
	
	public static void check(int given, int expected) {
		if (given != expected) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + given + " as expected.");
		}
	}		
	
	public static void check(short given, short expected) {
		if (given != expected) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + given + " as expected.");
		}
	}
	
	public static void check(byte given, byte expected) {
		if (given != expected) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + given + " as expected.");
		}
	}
	
	public static void check(char given, char expected) {
		if (given != expected) {
			testFail("Given '" + given + "' but expected '" + expected + "'");
		} else {
			testOk("Given '" + given + "' as expected.");
		}
	}
	
	public static void check(boolean given, boolean expected) {
		if (given != expected) {
			testFail("Given " + given + " but expected " + expected + ".");
		} else {
			testOk("Given " + given + " as expected.");
		}
	}
	
	public static void check(Object[] given, Object[] expected) {
		if (!Arrays.deepEquals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.deepToString(given) + 
				"\nbut expected\n    " + Arrays.deepToString(expected)
			);
		} else {
			testOk("Given " + Arrays.deepToString(given) + " as expected.");
		}	
	}
	
	
	public static void check(double[] given, double[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}
	
	public static void check(float[] given, float[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}

	
	public static void check(long[] given, long[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}	
	
	public static void check(int[] given, int[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}	
	
	public static void check(short[] given, short[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}
	
	public static void check(byte[] given, byte[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}
	
	public static void check(char[] given, char[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}
	
	public static void check(boolean[] given, boolean[] expected) {
		if (!Arrays.equals(given, expected)) {
			testFail(
				"\nGiven\n    " + Arrays.toString(given) + 
				"\nbut expected\n    " + Arrays.toString(expected)
			);
		} else {
			testOk("Given " + Arrays.toString(given) + " as expected.");
		}	
	}
	
	public static boolean objectEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}
	
	public static void check(String given, String expected) {
		if (!objectEquals(given, expected)) {
			testFail("\nGiven\n    \"" + given + "\"\nbut expected\n    \"" + expected + "\".");
		} else {
			testOk("Given \"" + given + "\" as expected.");
		}
	}	
		
    public static void check(Object given, Object expected) {
		if (given instanceof String && expected instanceof String) {
			check((String)given, (String)expected);
		} else if (given instanceof Object[] && expected instanceof Object[]) {
			check((Object[])given, (Object[])expected);
		} else if (given instanceof double[] && expected instanceof double[]) {
			check((double[])given, (double[])expected);
		} else if (given instanceof float[] && expected instanceof float[]) {
			check((float[])given, (float[])expected);
		} else if (given instanceof long[] && expected instanceof long[]) {
			check((long[])given, (long[])expected);
		} else if (given instanceof int[] && expected instanceof int[]) {
			check((int[])given, (int[])expected);
		} else if (given instanceof short[] && expected instanceof short[]) {
			check((short[])given, (short[])expected);
		} else if (given instanceof byte[] && expected instanceof byte[]) {
			check((byte[])given, (byte[])expected);		
		} else if (given instanceof char[] && expected instanceof char[]) {
			check((char[])given, (char[])expected);		
		} else if (given instanceof boolean[] && expected instanceof boolean[]) {
			check((boolean[])given, (boolean[])expected);					
		} else {
			if (!objectEquals(given, expected)) {
				testFail("Given " + given + " but expected " + expected + ".");
			} else {
				testOk("Given " + given + " as expected.");
			}
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
