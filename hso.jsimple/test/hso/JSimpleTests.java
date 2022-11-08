package hso;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

@FunctionalInterface
interface Action {
    void apply();
}

class A {
    private int f;
    private B g;
    A(int f, B g) {
        this.f = f;
        this.g = g;
    }
}

class B {
    private String s;
    B(String s) {
        this.s = s;
    }
}


record IntPoint(int x, int y) {
}

record Point(double x, double y) {
}

class JSimpleTests {

    @Test
    void testRound() {
        assertEquals(4, JSimple.round(3.5));
        assertEquals(3, JSimple.round(3.4));
    }

    @Test
    void testCeil() {
        assertEquals(4, JSimple.ceil(3.5));
        assertEquals(4, JSimple.ceil(3.4));
        assertEquals(4, JSimple.ceil(4.0));
    }
    
    @Test
    void testFloor() {
        assertEquals(3, JSimple.floor(3.5));
        assertEquals(3, JSimple.floor(3.4));
        assertEquals(3, JSimple.floor(3.9));
        assertEquals(4, JSimple.floor(4.0));
    }
    
    private void assertOk(Action action) {
       assertAction(action, 1, 0);
    }

    private void assertFail(Action action) {
        assertAction(action, 0, 1);
     }
    
    private void assertAction(Action action, int okCount, int failCount) {
        JSimple.resetTestCounts();
        action.apply();
        assertEquals(okCount, JSimple.getOkTestCount());
        assertEquals(failCount, JSimple.getFailTestCount());
    }
    
    @Test
    void testCheckPrim() {
        // long
        assertOk(() -> JSimple.check(1L, 1L));
        assertFail(() -> JSimple.check(1L, 2L));
        // int
        assertOk(() -> JSimple.check(1, 1));
        assertFail(() -> JSimple.check(1, 2));
        // char
        assertOk(() -> JSimple.check('a', 'a'));
        assertFail(() -> JSimple.check('a', 'b'));
        // short
        assertOk(() -> JSimple.check((short)1, (short)1));
        assertFail(() -> JSimple.check((short)1, (short)2));
        // byte
        assertOk(() -> JSimple.check((byte)1, (byte)1));
        assertFail(() -> JSimple.check((byte)1, (byte)2));
        // bool
        assertOk(() -> JSimple.check(true, true));
        assertFail(() -> JSimple.check(false, true));
        // double
        assertOk(() -> JSimple.check(3.14, 3.14));
        assertFail(() -> JSimple.check(3.14, 3.15));
        // float
        assertOk(() -> JSimple.check(3.14f, 3.14f));
        assertFail(() -> JSimple.check(3.14f, 3.15f));
    }
    
    @Test
    void testCheckPrimMixed() {
        // long/int
        assertOk(() -> JSimple.check(1L, 1));
        assertFail(() -> JSimple.check(1L, 2));
        assertOk(() -> JSimple.check(1, 1L));
        assertFail(() -> JSimple.check(1, 2L));
        // long/short
        assertOk(() -> JSimple.check(1L, (short)1));
        assertFail(() -> JSimple.check(1L, (short)2));
        assertOk(() -> JSimple.check((short)1, 1L));
        assertFail(() -> JSimple.check((short)1, 2L));
        // long/byte
        assertOk(() -> JSimple.check(1L, (byte)1));
        assertFail(() -> JSimple.check(1L, (byte)2));
        assertOk(() -> JSimple.check((byte)1, 1L));
        assertFail(() -> JSimple.check((byte)1, 2L));    
        // long/char
        assertOk(() -> JSimple.check(97L, 'a'));
        assertFail(() -> JSimple.check(97L, 'b'));
        assertOk(() -> JSimple.check('a', 97L));
        assertFail(() -> JSimple.check('b', 97L));   
        // long/float
        assertOk(() -> JSimple.check(1L, 1.0f));
        assertFail(() -> JSimple.check(1L, 2.0f));
        assertOk(() -> JSimple.check(1.0f, 1L));
        assertFail(() -> JSimple.check(1.0f, 2L));
        // long/double
        assertOk(() -> JSimple.check(1L, 1.0));
        assertFail(() -> JSimple.check(1L, 2.0));
        assertOk(() -> JSimple.check(1.0, 1L));
        assertFail(() -> JSimple.check(1.0, 2L));    
        
        // int/short
        assertOk(() -> JSimple.check(1, (short)1));
        assertFail(() -> JSimple.check(1, (short)2));
        assertOk(() -> JSimple.check((short)1, 1));
        assertFail(() -> JSimple.check((short)1, 2));
        // int/byte
        assertOk(() -> JSimple.check(1, (byte)1));
        assertFail(() -> JSimple.check(1, (byte)2));
        assertOk(() -> JSimple.check((byte)1, 1));
        assertFail(() -> JSimple.check((byte)1, 2));    
        // int/char
        assertOk(() -> JSimple.check(97, 'a'));
        assertFail(() -> JSimple.check(97, 'b'));
        assertOk(() -> JSimple.check('a', 97));
        assertFail(() -> JSimple.check('b', 97));   
        // int/float
        assertOk(() -> JSimple.check(1, 1.0f));
        assertFail(() -> JSimple.check(1, 2.0f));
        assertOk(() -> JSimple.check(1.0f, 1));
        assertFail(() -> JSimple.check(1.0f, 2));
        // int/double
        assertOk(() -> JSimple.check(1, 1.0));
        assertFail(() -> JSimple.check(1, 2.0));
        assertOk(() -> JSimple.check(1.0, 1));
        assertFail(() -> JSimple.check(1.0, 2));  
        
        // short/byte
        assertOk(() -> JSimple.check((short)1, (byte)1));
        assertFail(() -> JSimple.check((short)1, (byte)2));
        assertOk(() -> JSimple.check((byte)1, (short)1));
        assertFail(() -> JSimple.check((byte)1, (short)2));    
        // short/char
        assertOk(() -> JSimple.check((short)97, 'a'));
        assertFail(() -> JSimple.check((short)97, 'b'));
        assertOk(() -> JSimple.check('a', (short)97));
        assertFail(() -> JSimple.check('b', (short)97));   
        // short/float
        assertOk(() -> JSimple.check((short)1, 1.0f));
        assertFail(() -> JSimple.check((short)1, 2.0f));
        assertOk(() -> JSimple.check(1.0f, (short)1));
        assertFail(() -> JSimple.check(1.0f, (short)2));
        // short/double
        assertOk(() -> JSimple.check((short)1, 1.0));
        assertFail(() -> JSimple.check((short)1, 2.0));
        assertOk(() -> JSimple.check(1.0, (short)1));
        assertFail(() -> JSimple.check(1.0, (short)2));        
        
        // byte/char
        assertOk(() -> JSimple.check((byte)97, 'a'));
        assertFail(() -> JSimple.check((byte)97, 'b'));
        assertOk(() -> JSimple.check('a', (byte)97));
        assertFail(() -> JSimple.check('b', (byte)97));   
        // byte/float
        assertOk(() -> JSimple.check((byte)1, 1.0f));
        assertFail(() -> JSimple.check((byte)1, 2.0f));
        assertOk(() -> JSimple.check(1.0f, (byte)1));
        assertFail(() -> JSimple.check(1.0f, (byte)2));
        // byte/double
        assertOk(() -> JSimple.check((byte)1, 1.0));
        assertFail(() -> JSimple.check((byte)1, 2.0));
        assertOk(() -> JSimple.check(1.0, (byte)1));
        assertFail(() -> JSimple.check(1.0, (byte)2));  
        
        // char/float
        assertOk(() -> JSimple.check('a', 97.0f));
        assertFail(() -> JSimple.check('a', 2.0f));
        assertOk(() -> JSimple.check(97.0f, 'a'));
        assertFail(() -> JSimple.check(98.0f, 'a'));
        // char/double
        assertOk(() -> JSimple.check('a', 97.0));
        assertFail(() -> JSimple.check('a', 2.0));
        assertOk(() -> JSimple.check(97.0, 'a'));
        assertFail(() -> JSimple.check(98.0, 'a'));   
        
        // float/double
        assertOk(() -> JSimple.check(97.0f, 97.0));
        assertFail(() -> JSimple.check(1.0f, 2.0));
        assertOk(() -> JSimple.check(97.0, 97.0f));
        assertFail(() -> JSimple.check(98.0, 2.0f));           
    }
     
    @Test
    void testCheckObj() {
        // String
        assertOk(() -> JSimple.check("Stefan", new String("Stefan")));
        assertFail(() -> JSimple.check("foo", "bar"));
        // Object
        Object obj = new Object();
        assertOk(() -> JSimple.check(obj, obj));
        assertFail(() -> JSimple.check(obj, new Object()));
        // Custom class, without equals (structural equality)
        assertOk(() -> JSimple.check(new A(1, new B("foo")), new A(1, new B("foo"))));
        assertFail(() -> JSimple.check(new A(1, new B("foo")), new A(1, new B("foox"))));
        // Custom class, with equals
        assertOk(() -> JSimple.check(new IntPoint(1, 2), new IntPoint(1, 2)));
        assertFail(() -> JSimple.check(new IntPoint(1, 2), obj));
        assertOk(() -> JSimple.check(new Point(1.2, 2), new Point(1.2, 2)));
        assertFail(() -> JSimple.check(new Point(1.2, 2), obj));
    }
    
    @Test
    void testCheckPrimArray() {
        // Arrays
        // long
        assertOk(() -> JSimple.check(new long[] {1L}, new long[] {1L}));
        assertFail(() -> JSimple.check(1L, 2L));
        // int
        assertOk(() -> JSimple.check(new int[] {1}, new int[] {1}));
        assertFail(() -> JSimple.check(new int[] {1}, new int[] {2}));
        // char
        assertOk(() -> JSimple.check(new char[]{'a'}, new char[]{'a'}));
        assertFail(() -> JSimple.check(new char[]{'a'}, new char[]{'b'}));
        // short
        assertOk(() -> JSimple.check(new short[] {1}, new short[] {1}));
        assertFail(() -> JSimple.check(new short[] {1}, new short[] {2}));
        // byte
        assertOk(() -> JSimple.check(new byte[] {1}, new byte[] {1}));
        assertFail(() -> JSimple.check(new byte[] {1}, new byte[] {2}));
        // bool
        assertOk(() -> JSimple.check(new boolean[] {true}, new boolean[] {true}));
        assertFail(() -> JSimple.check(new boolean[] {true}, new boolean[] {false}));
        // double
        assertOk(() -> JSimple.check(new double[] {3.14}, new double[] {3.14}));
        assertFail(() -> JSimple.check(new double[] {3.14}, new double[] {3.15}));
        // float
        assertOk(() -> JSimple.check(new float[] {3.14f}, new float[] {3.14f}));
        assertFail(() -> JSimple.check(new float[] {3.14f}, new float[] {3.15f}));
    }
    
    @Test
    void testCheckObjArray() {
        // String
        assertOk(() -> JSimple.check(new String[]{"Stefan"}, new String[] {new String("Stefan")}));
        assertFail(() -> JSimple.check(new String[]{"foo"}, new String[]{"bar"}));
        // Object
        Object obj = new Object();
        assertOk(() -> JSimple.check(new Object[] {obj}, new Object[] {obj}));
        assertFail(() -> JSimple.check(new Object[] {obj}, new Object[] {new Object()}));
        // Custom class, without equals (structural equality)
        assertOk(() -> JSimple.check(new A[] {new A(1, new B("foo"))}, new A[] {new A(1, new B("foo"))}));
        assertFail(() -> JSimple.check(new A[] {new A(1, new B("foo"))}, new A[] {new A(1, new B("foox"))}));

        // Custom class, with equals
        assertOk(() -> JSimple.check(new IntPoint[] {new IntPoint(1, 2)}, new IntPoint[] {new IntPoint(1, 2)}));
        assertFail(() -> JSimple.check(new IntPoint[] {new IntPoint(1, 2)}, new IntPoint[] {new IntPoint(2, 1)}));
        assertOk(() -> JSimple.check(new Point[] {new Point(1.2, 2)}, new Point[] {new Point(1.2, 2)}));
        assertFail(() -> JSimple.check(new Point[] {new Point(1.2, 2)}, new Point[] {new Point(1.2001, 2)}));
    }
    
    @Test
    void testCheckNestedArray() {
        // Nested arrays 
        Object obj = new Object();
        assertOk(() -> JSimple.check(new int[][] {{1}}, new int[][] {{1}}));
        assertFail(() -> JSimple.check(new int[][] {{1}}, new int[][] {{2}}));
        assertOk(() -> JSimple.check(new Object[][] {{obj}}, new Object[][] {{obj}}));
        assertFail(() -> JSimple.check(new Object[][] {{obj}}, new Object[][] {{new Object()}}));
        // Custom class, with equals
        assertOk(() -> JSimple.check(new IntPoint[][] {{new IntPoint(1, 2)}}, new IntPoint[][] {{new IntPoint(1, 2)}}));
        assertFail(() -> JSimple.check(new IntPoint[][] {{new IntPoint(1, 2)}}, new IntPoint[][] {{new IntPoint(2, 1)}}));
        // Custom class, without equals (structural equality)
        assertOk(() -> JSimple.check(new A[][] {{new A(1, new B("foo"))}}, new A[][] {{new A(1, new B("foo"))}}));
        assertFail(() -> JSimple.check(new A[][] {{new A(1, new B("foo"))}}, new A[][] {{new A(1, new B("foox"))}}));
    }
}
