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

import static org.junit.Assert.assertTrue;

public class LWWSetTests {

    @Test
    public void testLookup() {
        final LWWSet<String> lwwSet = new LWWSet<>();

        lwwSet.add(new LWWSet.ElementState<>(1, "dog"));
        lwwSet.add(new LWWSet.ElementState<>(1, "cat"));
        lwwSet.add(new LWWSet.ElementState<>(1, "ape"));
        lwwSet.add(new LWWSet.ElementState<>(1, "tiger"));

        lwwSet.remove(new LWWSet.ElementState<>(2, "cat"));
        lwwSet.remove(new LWWSet.ElementState<>(2, "dog"));

        // Actual test
        final Set<String> lookup = lwwSet.lookup();

        assertTrue(lookup.size() == 2);
        assertTrue(lookup.contains("ape"));
        assertTrue(lookup.contains("tiger"));
    }

    @Test
    public void testMerge() {
        final LWWSet<String> firstLwwSet = new LWWSet<>();
        firstLwwSet.add(new LWWSet.ElementState<>(3, "ape"));
        firstLwwSet.add(new LWWSet.ElementState<>(1, "dog"));
        firstLwwSet.add(new LWWSet.ElementState<>(1, "cat"));
        firstLwwSet.remove(new LWWSet.ElementState<>(2, "cat"));

        final LWWSet<String> secondLwwSet = new LWWSet<>();
        secondLwwSet.add(new LWWSet.ElementState<>(1, "ape"));
        secondLwwSet.add(new LWWSet.ElementState<>(1, "tiger"));
        secondLwwSet.add(new LWWSet.ElementState<>(1, "cat"));
        secondLwwSet.remove(new LWWSet.ElementState<>(2, "ape"));

        // Actual test
        final LWWSet<String> resultSet = firstLwwSet.merge(secondLwwSet);

        final GSet<LWWSet.ElementState<String>> resultAddSet = resultSet.getAddSet();
        final Set<LWWSet.ElementState<String>> addLookup = resultAddSet.lookup();
        assertTrue(addLookup.size() == 5);
        addLookup.contains(new LWWSet.ElementState<>(1, "ape"));
        addLookup.contains(new LWWSet.ElementState<>(3, "ape"));
        addLookup.contains(new LWWSet.ElementState<>(1, "dog"));
        addLookup.contains(new LWWSet.ElementState<>(1, "tiger"));
        addLookup.contains(new LWWSet.ElementState<>(1, "cat"));

        final GSet<LWWSet.ElementState<String>> resultRemoveSet = resultSet.getRemoveSet();
        final Set<LWWSet.ElementState<String>> removeLookup = resultRemoveSet.lookup();
        assertTrue(removeLookup.size() == 2);
        addLookup.contains(new LWWSet.ElementState<>(2, "cat"));
        addLookup.contains(new LWWSet.ElementState<>(2, "ape"));
    }

    @Test
    public void testDiff() {
        final LWWSet<String> firstLwwSet = new LWWSet<>();
        firstLwwSet.add(new LWWSet.ElementState<>(3, "ape"));
        firstLwwSet.add(new LWWSet.ElementState<>(1, "dog"));
        firstLwwSet.add(new LWWSet.ElementState<>(2, "tiger"));
        firstLwwSet.add(new LWWSet.ElementState<>(1, "cat"));
        firstLwwSet.remove(new LWWSet.ElementState<>(2, "cat"));

        final LWWSet<String> secondLwwSet = new LWWSet<>();
        secondLwwSet.add(new LWWSet.ElementState<>(1, "ape"));
        secondLwwSet.add(new LWWSet.ElementState<>(3, "tiger"));
        secondLwwSet.add(new LWWSet.ElementState<>(1, "cat"));
        secondLwwSet.remove(new LWWSet.ElementState<>(2, "ape"));

        // Actual test
        final LWWSet<String> resultSet = firstLwwSet.diff(secondLwwSet);

        final GSet<LWWSet.ElementState<String>> resultAddSet = resultSet.getAddSet();
        assertTrue(resultAddSet.lookup().size() == 3);
        resultAddSet.lookup().contains(new LWWSet.ElementState<>(3, "ape"));
        resultAddSet.lookup().contains(new LWWSet.ElementState<>(1, "dog"));
        resultAddSet.lookup().contains(new LWWSet.ElementState<>(2, "tiger"));

        final GSet<LWWSet.ElementState<String>> resultRemoveSet = resultSet.getRemoveSet();
        assertTrue(resultRemoveSet.lookup().size() == 1);
        resultRemoveSet.lookup().contains(new LWWSet.ElementState<>(2, "cat"));
    }
}
