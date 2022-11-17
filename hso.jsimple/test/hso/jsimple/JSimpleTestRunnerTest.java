package hso.jsimple;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hso.JSimple;
import hso.TodoException;

class C1 {
    public static void main(String[] args) {
        JSimple.check(1, 1);
        JSimple.check(1, 2);
    }
}

class C2 {
    public static void main(String[] args) {
        JSimple.check(1, 1);
        throw JSimple.todo();
    }
}

class JSimpleTestRunnerTest {

    @Test
    void testTestRunner() {
        JSimple.resetTestCounts();
        String srcFile = "test/hso/jsimple/JSimpleTestRunnerTest.java";
        JSimpleCmdlineArgs args1 = new JSimpleCmdlineArgs(false, srcFile);
        JSimpleTestResult r1 = JSimpleTestRunner.runAllTests(args1);
        assertEquals(new JSimpleTestResult(2, 1, 3, true), r1);
    }

}
