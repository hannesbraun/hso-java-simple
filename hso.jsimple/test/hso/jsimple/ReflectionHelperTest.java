package hso.jsimple;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.*;

class A {
    private int f;
    A(int f) {
        this.f = f;
    }
    int getF() {
        return f;
    }
}

class B extends A {
    private String g;
    B(String g, int f) {
        super(f);
        this.g = g;
    }
    String getG() {
        return this.g;
    }
}

class C extends A {
    private String g;
    C(String g, int f) {
        super(f);
        this.g = g;
    }
    String getG() {
        return this.g;
    }
}

class Node {
    Node left, right;
    int value;
    Node(int value) {
        this.value = value;
    }
    Node(Node left, int value, Node right) {
        this.left = left;
        this.value = value;
        this.right = right;
    }
}

class ReflectionHelperTest {

    @Test
    void testObjectAsMap() throws ReflectionException {
        B b = new B("foo", 42);
        Map<String, Object> m = ReflectionHelper.objectAsMap(b);
        assertEquals(2, m.size());
        assertEquals("foo", m.get("g"));
        assertEquals(Integer.valueOf(42), m.get("f"));
    }
    
    @Test
    void testArrayAsMap() throws ReflectionException {
        Map<String, Object> m = ReflectionHelper.objectAsMap(new int[] {3,0});
        assertEquals(2, m.size());
        assertEquals(3, m.get("0"));
        assertEquals(0, m.get("1"));

        m = ReflectionHelper.objectAsMap(new double[] {3.13,0});
        assertEquals(2, m.size());
        assertEquals(3.13, m.get("0"));
        assertEquals(0.0, m.get("1"));
        
        m = ReflectionHelper.objectAsMap(new boolean[] {false});
        assertEquals(1, m.size());
        assertEquals(false, m.get("0"));
    }
    
    @Test
    void testGenericEquality() throws ReflectionException {
        B b1 = new B("foo", 42);
        B b2 = new B("foo", 42);
        B b3 = new B("foo", 43);
        B b4 = new B(null, 43);
        B b5 = new B(null, 43);
        C c = new C("foo", 42);
        assertEquals(true, ReflectionHelper.genericEquality(null, null));
        assertEquals(false, ReflectionHelper.genericEquality(null, b1));
        assertEquals(false, ReflectionHelper.genericEquality(b1, null));
        assertEquals(true, ReflectionHelper.genericEquality(b1, b2));
        assertEquals(false, ReflectionHelper.genericEquality(b1, b3));
        assertEquals(true, ReflectionHelper.genericEquality(b4, b5));
        assertEquals(false, ReflectionHelper.genericEquality(b1, c));
        
        Node n1 = new Node(new Node(1), 2, new Node(3));
        n1.right.left = n1; // circular
        Node n2 = new Node(new Node(1), 2, new Node(3));
        n2.right.left = n2;
        assertEquals(false, ReflectionHelper.genericEquality(n1, n2));
        
        B[] bs1 = new B[] {b1, b2};
        B[] bs2 = new B[] {b1, b2};
        B[] bs3 = new B[] {b2, b1};  
        B[] bs4 = new B[] {b1, b3};  
        assertEquals(true, ReflectionHelper.genericEquality(bs1, bs2));
        assertEquals(true, ReflectionHelper.genericEquality(bs1, bs3));
        assertEquals(false, ReflectionHelper.genericEquality(bs1, bs4));
        B[] bs5 = new B[] {b1, b1};
        B[] bs6 = new B[] {b2, b2};
        assertEquals(true, ReflectionHelper.genericEquality(bs5, bs6));
        
        B[][] bss1 = new B[][] {bs1, bs2};
        B[][] bss2 = new B[][] {bs1, bs2};
        B[][] bss3 = new B[][] {bs2, bs1};
        B[][] bss4 = new B[][] {bs1, bs4};
        assertEquals(true, ReflectionHelper.genericEquality(bss1, bss2));
        assertEquals(true, ReflectionHelper.genericEquality(bss1, bss3));
        assertEquals(false, ReflectionHelper.genericEquality(bss1, bss4));
    }
    
    @Test
    void testGenericToString() throws ReflectionException {
        B b1 = new B("foo", 42);
        B b2 = new B(null, 43);
        assertEquals("null", ReflectionHelper.genericToString(null));
        assertEquals("B[f=42, g=\"foo\"]", ReflectionHelper.genericToString(b1));
        assertEquals("B[f=43, g=null]", ReflectionHelper.genericToString(b2));
        Node n1 = new Node(new Node(1), 2, new Node(3));
        n1.right = n1; // circular
        assertEquals("Node[left=Node[left=null, right=null, value=1], right=<circular>, value=2]",
                ReflectionHelper.genericToString(n1));
        
        B[] bs1 = new B[] {b1, b2};
        assertEquals("[B[f=42, g=\"foo\"], B[f=43, g=null]]", ReflectionHelper.genericToString(bs1));
        
        B[][] bss1 = new B[][] {bs1};
        assertEquals("[[B[f=42, g=\"foo\"], B[f=43, g=null]]]", ReflectionHelper.genericToString(bss1));
        
        List<String> l = new ArrayList<String>();
        l.add("foo");
        l.add(null);
        l.add("");
        l.add("");
        l.add("\"hi\"\n\t\\");
        String expected = """
                            ["foo", null, "", "", "\\"hi\\"\\n\\t\\\\"]               
                          """.strip();
        assertEquals(expected, ReflectionHelper.genericToString(l));
    }

}
