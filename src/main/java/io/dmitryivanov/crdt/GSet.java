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

public class GSet<E> {

    private Set<E> set;

    public GSet() {
        set = new HashSet<>();
    }

    GSet(Set<E> set) {
        this.set = new HashSet<>(set);
    }

    public void add(final E elem) {
        set.add(elem);
    }

    public Set<E> lookup() {
        return Collections.unmodifiableSet(set);
    }

    public GSet<E> merge(GSet<E> anotherGSet) {
        final HashSet<E> newSet = new HashSet<>(set);
        newSet.addAll(anotherGSet.getSet());
        return new GSet<>(newSet);
    }

    public GSet<E> diff(GSet<E> anotherGSet) {
        return new GSet<>(Operations.diff(set, anotherGSet.lookup()));
    }

    // Visible for testing
    Set<E> getSet() {
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GSet<?> gSet = (GSet<?>) o;

        return set.equals(gSet.set);

    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }
}
