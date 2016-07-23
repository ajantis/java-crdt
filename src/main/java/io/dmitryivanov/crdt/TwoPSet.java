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

import io.dmitryivanov.crdt.helpers.Operations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TwoPSet<E> {

    private GSet<E> addSet;

    private GSet<E> removeSet;

    public TwoPSet() {
        addSet = new GSet<>();
        removeSet = new GSet<>();
    }

    private TwoPSet(GSet<E> addSet, GSet<E> removeSet) {
        this.addSet = new GSet<>(addSet.lookup());
        this.removeSet = new GSet<>(removeSet.lookup());
    }

    public void add(final E element) {
        addSet.add(element);
    }

    public void remove(final E element) {
        removeSet.add(element);
    }

    public Set<E> lookup() {
        Set<E> resultSet = Operations.diff(addSet.lookup(), removeSet.lookup());
        return Collections.unmodifiableSet(resultSet);
    }

    public TwoPSet<E> merge(TwoPSet<E> another2PSet) {
        return new TwoPSet<>(addSet.merge(another2PSet.addSet), removeSet.merge(another2PSet.removeSet));
    }

    public TwoPSet<E> diff(TwoPSet<E> anotherSet) {
        return new TwoPSet<>(addSet.diff(anotherSet.addSet), removeSet.diff(anotherSet.removeSet));
    }

    private Set<E> union(HashSet<E> firstSet, HashSet<E> secondSet) {
        final HashSet<E> result = new HashSet<>();
        result.addAll(firstSet);
        result.addAll(secondSet);
        return Collections.unmodifiableSet(result);
    }

    private Set<E> diff(HashSet<E> firstSet, HashSet<E> secondSet) {
        return Operations.diff(firstSet, secondSet);
    }

    // Visible for testing
    GSet<E> getAddSet() {
        return new GSet<>(addSet.lookup());
    }

    // Visible for testing
    GSet<E> getRemoveSet() {
        return new GSet<>(removeSet.lookup());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwoPSet<?> twoPSet = (TwoPSet<?>) o;

        return addSet.equals(twoPSet.addSet) && removeSet.equals(twoPSet.removeSet);
    }

    @Override
    public int hashCode() {
        int result = addSet.hashCode();
        result = 31 * result + removeSet.hashCode();
        return result;
    }
}
