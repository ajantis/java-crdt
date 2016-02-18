package io.dmitryivanov.crdt;

import org.junit.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OURSetTests {

    @Test
    public void testLookup() {
        OURSet<String> ourSet = new OURSet<>();

        final OURSet.ElementState<String> ape = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "ape");
        final OURSet.ElementState<String> dog = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "dog");
        final OURSet.ElementState<String> cat = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "cat");

        ourSet.add(ape);
        ourSet.add(dog);
        ourSet.add(cat);

        ourSet.remove(new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "tiger"));
        ourSet.remove(new OURSet.ElementState<>(cat.getId(), System.currentTimeMillis() + 1, cat.getElement()));

        // Actual test
        Set<String> lookupResult = ourSet.lookup();

        assertEquals(lookupResult.size(), 2);
        assertTrue(lookupResult.contains("dog"));
        assertTrue(lookupResult.contains("ape"));
    }

    @Test
    public void testMerge() {

        final OURSet.ElementState<String> ape = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "ape");
        final OURSet.ElementState<String> dog = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "dog");
        final OURSet.ElementState<String> cat = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "cat");
        final OURSet.ElementState<String> tiger = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "tiger");
        final OURSet.ElementState<String> removedCat = new OURSet.ElementState<>(cat.getId(), true, cat.getTimestamp() + 1, "cat");

        OURSet<String> firstOURSet = new OURSet<>();

        firstOURSet.add(ape);
        firstOURSet.add(dog);
        firstOURSet.remove(removedCat);

        OURSet<String> secondOURSet = new OURSet<>();
        secondOURSet.add(cat);
        secondOURSet.add(tiger);

        // Actual test
        OURSet<String> mergeResult = firstOURSet.merge(secondOURSet);

        assertEquals(mergeResult.getElements().size(), 4);
        mergeResult.getElements().contains(ape);
        mergeResult.getElements().contains(dog);
        mergeResult.getElements().contains(tiger);
        mergeResult.getElements().contains(removedCat);

        OURSet<String> reverseMergeResult = secondOURSet.merge(firstOURSet);
        assertEquals("'merge' should be symmetrical", mergeResult, reverseMergeResult);
    }

    @Test
    public void testDiff() {
        final OURSet.ElementState<String> ape = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "ape");
        final OURSet.ElementState<String> dog = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "dog");
        final OURSet.ElementState<String> cat = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "cat");
        final OURSet.ElementState<String> tiger = new OURSet.ElementState<>(UUID.randomUUID(), System.currentTimeMillis(), "tiger");
        final OURSet.ElementState<String> removedCat = new OURSet.ElementState<>(cat.getId(), true, cat.getTimestamp() + 1, "cat");

        OURSet<String> firstOURSet = new OURSet<>();

        firstOURSet.add(ape);
        firstOURSet.add(dog);
        firstOURSet.remove(removedCat);

        OURSet<String> secondOURSet = new OURSet<>();
        secondOURSet.add(dog);
        secondOURSet.add(cat);
        secondOURSet.add(tiger);

        // Actual test
        OURSet<String> diffResult = firstOURSet.diff(secondOURSet);

        assertEquals(diffResult.getElements().size(), 2);
        diffResult.getElements().contains(ape);
        diffResult.getElements().contains(removedCat);

        // Reverse diff
        OURSet<String> reverseDiffResult = secondOURSet.diff(firstOURSet);

        assertEquals(reverseDiffResult.getElements().size(), 1);
        reverseDiffResult.getElements().contains(tiger);
    }
}
