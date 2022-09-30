package hso;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
}
