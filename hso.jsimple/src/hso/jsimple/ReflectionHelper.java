package hso.jsimple;

import java.util.*;
import java.lang.reflect.*;

// Wrapper class that implements equals by comparing the wrapped obj with ==
final class SeenEntry {
    private Object obj;
    SeenEntry(Object obj) {
        this.obj = obj;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof SeenEntry otherEntry) {
            return this.obj == otherEntry.obj;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return System.identityHashCode(this.obj);
    }
}

final class RecursionGuard {
    private int calls;
    private Set<SeenEntry> seen;
    private final int MAX_CALLS = 100;
    RecursionGuard(Object obj) {
        this.calls = 0;
        this.seen = new HashSet<SeenEntry>();
        if (obj != null) {
            this.seen.add(new SeenEntry(obj));
        }
    }
    boolean checkRecursiveCall(Object arg) {
        if (this.calls + 1 > MAX_CALLS) {
            return false;
        }
        if (arg == null) {
            return true;
        }
        SeenEntry e = new SeenEntry(arg);
        if (this.seen.contains(e)) {
            return false;
        }
        this.calls++;
        this.seen.add(e);
        return true;
    }
}

public class ReflectionHelper {

    private static List<Class<?>> getSuperclassChain(Class<?> cls) {
        List<Class<?>> lst = new ArrayList<Class<?>>();
        while (cls != null) {
            lst.add(cls);
            cls = cls.getSuperclass();
        }
        return lst;
    }
    
    private static Object[] objectAsArray(Object obj) {
        if (obj instanceof Object[] arr) {
            Object[] result = new Object[arr.length];
            System.arraycopy(arr, 0, result, 0, arr.length);
            return result;
        } else if (obj instanceof long[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;
        } else if (obj instanceof int[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;
        } else if (obj instanceof char[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;              
        } else if (obj instanceof short[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;              
        } else if (obj instanceof byte[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;               
        } else if (obj instanceof double[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;             
        } else if (obj instanceof float[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;               
        } else if (obj instanceof boolean[] arr) {
            Object[] result = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i];
            }                
            return result;               
        } else {
            Logger.error("Not an arry or array of unknown type: " + obj);
            return new Object[0];
        }
    }
    private static Map<String, Object> arrayAsMap(Object obj) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Object[] arr = objectAsArray(obj);
        for (int i = 0; i < arr.length; i++) {
            valueMap.put(i + "", arr[i]);
        }
        return valueMap;
    }
    
    /**
     * Returns a map containing values for all fields of the given object.
     */
    public static Map<String, Object> objectAsMap(Object obj) throws ReflectionException {
        if (obj.getClass().isArray()) {
            return arrayAsMap(obj);
        }
        List<Class<?>> lst = getSuperclassChain(obj.getClass());
        Map<String, Object> valueMap = new HashMap<String, Object>();
        for (Class<?> cls : lst) {
            if (Object.class.equals(cls)) {
                break;
            }
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    valueMap.put(field.getName(), value);
                } catch (IllegalArgumentException e) {
                    throw new ReflectionException(e);
                } catch (IllegalAccessException e) {
                    throw new ReflectionException(e);
                }
            }
        }
        return valueMap;
    }
    
    private static boolean declaresMethod(Object obj, String name, Class<?>... parameterTypes) {
        List<Class<?>> lst = getSuperclassChain(obj.getClass());
        for (Class<?> cls : lst) {
            if (Object.class.equals(cls)) {
                break;
            }        
            try {
                cls.getDeclaredMethod(name, parameterTypes);
                return true;
            } catch (NoSuchMethodException e) {
                // ignore: expected
            } catch (SecurityException e) {
                Logger.error("SecurityException: " + e);
            }
        }
        return false;
    }
    
    public static String genericToString(Object obj) {
        return genericToString(obj, new RecursionGuard(obj));
    }
    
    private static String genericArrayToString(Object obj, RecursionGuard g) {
        Object[] arr = objectAsArray(obj);
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(genericToString(arr[i], g));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static String genericToString(Object obj, RecursionGuard g) {
        if (obj == null) {
            return "null";
        }
        Class<?> cls = obj.getClass();
        if (declaresMethod(obj, "toString") || cls.equals(Object.class)) {
            return obj.toString();
        }
        if (cls.isArray()) {
            return genericArrayToString(obj, g);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(cls.getSimpleName());
        sb.append("[");
        Map<String, Object> m;
        try {
            m = objectAsMap(obj);
        } catch (ReflectionException e) {
            Logger.error("ReflectionException: " + e);
            sb.append("<error>]");
            return sb.toString();
        }
        Object[] keys = m.keySet().toArray();
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            String k = (String)keys[i];
            Object v = m.get(k);
            String s;
            if (g.checkRecursiveCall(v)) {
                s = genericToString(v, g);
            } else {
                s = "<circular>";
            }
            sb.append(k);
            sb.append("=");
            sb.append(s);
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static boolean genericEquality(Object obj1, Object obj2) {
        return genericEquality(obj1, new RecursionGuard(obj1), obj2, new RecursionGuard(obj2));
    }
    
    private static Long asLong(Object obj) {
        if (obj instanceof Long l) {
            return l;
        } else if (obj instanceof Integer i) {
            return i.longValue();
        } else if (obj instanceof Short s) {
            return s.longValue();
        } else if (obj instanceof Byte b) {
            return b.longValue();
        } else if (obj instanceof Character c) {
            return (long)c.charValue();
        } else {
            return null;
        }
    }
    
    private static Double asDouble(Object obj) {
        if (obj instanceof Double d) {
            return d;
        } else if (obj instanceof Float f) {
            return f.doubleValue();
        } else {
            Long l = asLong(obj);
            if (l != null) {
                return l.doubleValue();
            } else {
                return null;
            }
        }
    }
    
    private static double EPSILON = 0.0000001;
    
    @SuppressWarnings("rawtypes")
    private static boolean genericListEquality(List lst1, RecursionGuard g1, List lst2, RecursionGuard g2) {
        int n = lst1.size();
        if (n != lst2.size()) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            Object obj1 = lst1.get(i);
            Object obj2 = lst2.get(i);
            if (g1.checkRecursiveCall(obj1) && g2.checkRecursiveCall(obj2)) {
                if (!genericEquality(obj1, g1, obj2, g2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    } 
    
    @SuppressWarnings("rawtypes")
    private static boolean genericEquality(Object obj1, RecursionGuard g1, Object obj2, RecursionGuard g2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        Long l1 = asLong(obj1);
        Long l2 = asLong(obj2);
        if (l1 != null && l2 != null) {
            return l1.equals(l2);
        }
        Double d1 = asDouble(obj1);
        Double d2 = asDouble(obj2);
        if (d1 != null && d2 != null) {
            return Math.abs(d1 - d2) <= EPSILON;
        }
        if (obj1 instanceof List lst1 && obj2 instanceof List lst2) {
            return genericListEquality(lst1, g1, lst2, g2);
        }
        if (declaresMethod(obj1, "equals", Object.class)) {
            return obj1.equals(obj2);
        }
        if (declaresMethod(obj2, "equals", Object.class)) {
            return obj2.equals(obj1);
        }
        Class<?> cls1 = obj1.getClass();
        Class<?> cls2 = obj2.getClass();
        if (!cls1.isArray() && 
                (cls1.getPackageName().startsWith("java.") || 
                 cls1.getPackageName().startsWith("javax")) 
            ||
            !cls2.isArray() &&
                (cls2.getPackageName().startsWith("java.") || 
                 cls2.getPackageName().startsWith("javax"))
           ) {
            return obj1.equals(obj2);
        }
        if (!cls1.equals(cls2)) {
            return false;
        }
        Map<String, Object> m1;
        Map<String, Object> m2;
        try {
            m1 = objectAsMap(obj1);
            m2 = objectAsMap(obj2);
        } catch (ReflectionException e) {
            Logger.error("ReflectionException: " + e);
            return false;
        }
        if (!m1.keySet().equals(m2.keySet())) {
            return false;
        }
        for (Map.Entry<String, Object> e1 : m1.entrySet()) {
            Object v1 = e1.getValue();
            Object v2 = m2.get(e1.getKey());
            if (g1.checkRecursiveCall(v1) && g2.checkRecursiveCall(v2)) {
                if (!genericEquality(v1, g1, v2, g2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
