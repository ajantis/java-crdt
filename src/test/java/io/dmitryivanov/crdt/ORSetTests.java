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

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ORSetTests {

    @Test
    public void testLookup() {
        final ORSet<String> orSet = new ORSet<>();

        orSet.add(new ORSet.ElementState<>("#a", "dog"));
        orSet.add(new ORSet.ElementState<>("#b", "cat"));
        orSet.add(new ORSet.ElementState<>("#c", "ape"));
        orSet.add(new ORSet.ElementState<>("#c", "tiger"));

        orSet.remove(new ORSet.ElementState<>("#a", "dog"));
        orSet.remove(new ORSet.ElementState<>("#b", "cat"));

        // Actual test
        final Set<String> lookup = orSet.lookup();

        assertTrue(lookup.size() == 2);
        assertTrue(lookup.contains("ape"));
        assertTrue(lookup.contains("tiger"));
    }

    @Test
    public void testMerge() {
        final ORSet<String> firstORSet = new ORSet<>();
        firstORSet.add(new ORSet.ElementState<>("#b", "ape"));
        firstORSet.add(new ORSet.ElementState<>("#c", "dog"));
        firstORSet.add(new ORSet.ElementState<>("#d", "cat"));
        firstORSet.remove(new ORSet.ElementState<>("#d", "cat"));

        final ORSet<String> secondORSet = new ORSet<>();
        secondORSet.add(new ORSet.ElementState<>("#a", "ape"));
        secondORSet.add(new ORSet.ElementState<>("#h", "tiger"));
        secondORSet.add(new ORSet.ElementState<>("#d", "cat"));
        secondORSet.remove(new ORSet.ElementState<>("#a", "ape"));

        // Actual test
        final ORSet<String> resultSet = firstORSet.merge(secondORSet);

        final GSet<ORSet.ElementState<String>> resultAddSet = resultSet.getAddSet();
        final Set<ORSet.ElementState<String>> addLookup = resultAddSet.lookup();
        assertTrue(addLookup.size() == 5);
        addLookup.contains(new ORSet.ElementState<>("#a", "ape"));
        addLookup.contains(new ORSet.ElementState<>("#b", "ape"));
        addLookup.contains(new ORSet.ElementState<>("#c", "dog"));
        addLookup.contains(new ORSet.ElementState<>("#d", "cat"));
        addLookup.contains(new ORSet.ElementState<>("#h", "tiger"));

        final GSet<ORSet.ElementState<String>> resultRemoveSet = resultSet.getRemoveSet();
        final Set<ORSet.ElementState<String>> removeLookup = resultRemoveSet.lookup();
        assertTrue(removeLookup.size() == 2);
        addLookup.contains(new ORSet.ElementState<>("#d", "cat"));
        addLookup.contains(new ORSet.ElementState<>("#a", "ape"));

        ORSet<String> reverseResultSet = secondORSet.merge(firstORSet);
        assertEquals(resultSet, reverseResultSet);
    }

    @Test
    public void testDiff() {
        final ORSet<String> firstORSet = new ORSet<>();
        firstORSet.add(new ORSet.ElementState<>("#b", "ape"));
        firstORSet.add(new ORSet.ElementState<>("#c", "dog"));
        firstORSet.add(new ORSet.ElementState<>("#d", "cat"));
        firstORSet.remove(new ORSet.ElementState<>("#d", "cat"));

        final ORSet<String> secondORSet = new ORSet<>();
        secondORSet.add(new ORSet.ElementState<>("#a", "ape"));
        secondORSet.add(new ORSet.ElementState<>("#h", "tiger"));
        secondORSet.add(new ORSet.ElementState<>("#d", "cat"));
        secondORSet.remove(new ORSet.ElementState<>("#a", "ape"));

        // Actual test
        final ORSet<String> resultSet = firstORSet.diff(secondORSet);

        final GSet<ORSet.ElementState<String>> resultAddSet = resultSet.getAddSet();
        assertTrue(resultAddSet.lookup().size() == 2);
        resultAddSet.lookup().contains(new ORSet.ElementState<>("#b", "ape"));
        resultAddSet.lookup().contains(new ORSet.ElementState<>("#c", "dog"));

        final GSet<ORSet.ElementState<String>> resultRemoveSet = resultSet.getRemoveSet();
        assertTrue(resultRemoveSet.lookup().size() == 1);
        resultRemoveSet.lookup().contains(new ORSet.ElementState<>("#d", "cat"));
    }
}
