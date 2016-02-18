package io.dmitryivanov.crdt;

import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GSetTests {

    @Test
    public void testLookup() {
        GSet<String> gSet = new GSet<>();

        gSet.add("dog");
        gSet.add("ape");
        gSet.add("cat");

        // Actual test
        Set<String> result = gSet.lookup();

        assertTrue(result.size() == 3);
        assertTrue(result.contains("ape"));
        assertTrue(result.contains("dog"));
        assertTrue(result.contains("cat"));
    }

    @Test
    public void testMerge() {
        GSet<String> firstGSet = new GSet<>();
        firstGSet.add("dog");
        firstGSet.add("ape");

        GSet<String> secondGSet = new GSet<>();
        secondGSet.add("cat");
        secondGSet.add("dog");

        // Actual test
        GSet<String> result = firstGSet.merge(secondGSet);

        assertTrue(result.lookup().size() == 3);
        assertTrue(result.lookup().contains("dog"));
        assertTrue(result.lookup().contains("cat"));
        assertTrue(result.lookup().contains("dog"));

        GSet<String> reverseResult = secondGSet.merge(firstGSet);

        assertEquals(result, reverseResult);
    }

    @Test
    public void testDiff() {
        GSet<String> firstGSet = new GSet<>();
        firstGSet.add("dog");
        firstGSet.add("ape");

        GSet<String> secondGSet = new GSet<>();
        secondGSet.add("cat");
        secondGSet.add("dog");

        GSet<String> result = firstGSet.diff(secondGSet);

        assertTrue(result.lookup().size() == 1);
        assertTrue(result.lookup().contains("ape"));
    }
}
