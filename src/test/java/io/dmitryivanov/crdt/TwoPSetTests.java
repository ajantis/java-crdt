/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Dmitry Ivanov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.dmitryivanov.crdt;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwoPSetTests {

    @Test
    public void testLookup() {
        final TwoPSet<String> twoPSet = new TwoPSet<>();

        twoPSet.add("dog");
        twoPSet.add("cat");
        twoPSet.add("ape");
        twoPSet.add("tiger");

        twoPSet.remove("cat");
        twoPSet.remove("dog");

        // Actual test
        final Set<String> lookup = twoPSet.lookup();

        assertTrue(lookup.size() == 2);
        assertTrue(lookup.contains("ape"));
        assertTrue(lookup.contains("tiger"));
    }

    @Test
    public void testMerge() {
        final TwoPSet<String> firstTwoPSet = new TwoPSet<>();
        firstTwoPSet.add("ape");
        firstTwoPSet.add("dog");
        firstTwoPSet.add("cat");
        firstTwoPSet.remove("cat");

        final TwoPSet<String> secondTwoPSet = new TwoPSet<>();
        secondTwoPSet.add("ape");
        secondTwoPSet.add("tiger");
        secondTwoPSet.add("cat");
        secondTwoPSet.remove("ape");

        // Actual test
        final TwoPSet<String> resultSet = firstTwoPSet.merge(secondTwoPSet);

        final GSet<String> resultAddSet = resultSet.getAddSet();
        final Set<String> resultAddSetLookup = resultAddSet.lookup();
        assertTrue(resultAddSetLookup.size() == 4);
        resultAddSetLookup.contains("ape");
        resultAddSetLookup.contains("dog");
        resultAddSetLookup.contains("cat");
        resultAddSetLookup.contains("tiger");

        final GSet<String> resultRemoveSet = resultSet.getRemoveSet();
        final Set<String> resultRemoveSetLookup = resultRemoveSet.lookup();
        assertTrue(resultRemoveSetLookup.size() == 2);
        resultRemoveSetLookup.contains("cat");
        resultRemoveSetLookup.contains("ape");

        final TwoPSet<String> reverseResult = secondTwoPSet.merge(firstTwoPSet);
        assertEquals(resultSet, reverseResult);

        final TwoPSet<String> mergeItself = firstTwoPSet.merge(firstTwoPSet);
        assertEquals(firstTwoPSet, mergeItself);
    }

    @Test
    public void testDiff() {
        final TwoPSet<String> firstTwoPSet = new TwoPSet<>();
        firstTwoPSet.add("ape");
        firstTwoPSet.add("dog");
        firstTwoPSet.add("cat");
        firstTwoPSet.remove("cat");

        final TwoPSet<String> secondTwoPSet = new TwoPSet<>();
        secondTwoPSet.add("ape");
        secondTwoPSet.add("tiger");
        secondTwoPSet.add("cat");
        secondTwoPSet.remove("ape");

        // Actual test
        final TwoPSet<String> resultSet = firstTwoPSet.diff(secondTwoPSet);

        final GSet<String> resultAddSet = resultSet.getAddSet();
        assertTrue(resultAddSet.lookup().size() == 1);
        resultAddSet.lookup().contains("dog");

        final GSet<String> resultRemoveSet = resultSet.getRemoveSet();
        assertTrue(resultRemoveSet.lookup().size() == 1);
        resultRemoveSet.lookup().contains("cat");

        // Reverse diff
        final TwoPSet<String> resultSet2 = secondTwoPSet.diff(firstTwoPSet);

        final GSet<String> resultAddSet2 = resultSet2.getAddSet();
        assertTrue(resultAddSet2.lookup().size() == 1);
        resultAddSet2.lookup().contains("tiger");

        final GSet<String> resultRemoveSet2 = resultSet.getRemoveSet();
        assertTrue(resultRemoveSet2.lookup().size() == 1);
        resultRemoveSet2.lookup().contains("ape");
    }
}
