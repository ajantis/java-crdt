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

import io.dmitryivanov.crdt.helpers.ComparisonChain;
import io.dmitryivanov.crdt.helpers.Operations;

import java.util.*;

public class OURSet<E extends Comparable<E>> {

    private Set<ElementState<E>> elements = new HashSet<>();

    public static class ElementState<E extends Comparable<E>> implements Comparable<ElementState<E>> {
        private UUID id;
        private boolean removed;
        private long timestamp;
        private E element;

        public ElementState(UUID id, boolean removed, long timestamp, E element) {
            this.id = id;
            this.removed = removed;
            this.timestamp = timestamp;
            this.element = element;
        }

        public ElementState(UUID id, long timestamp, E element) {
            this(id, false, timestamp, element);
        }

        @Override
        public int compareTo(ElementState<E> anotherElementState) {
            return ComparisonChain.start()
                    .compare(getId(), anotherElementState.getId())
                    .compare(getTimestamp(), anotherElementState.getTimestamp())
                    .compareFalseFirst(isRemoved(), anotherElementState.isRemoved())
                    .compare(getElement(), anotherElementState.getElement())
                    .result();
        }

        public UUID getId() {
            return id;
        }

        public boolean isRemoved() {
            return removed;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public E getElement() {
            return element;
        }
    }

    public OURSet() {
        elements = new HashSet<>();
    }

    OURSet(Collection<ElementState<E>> elementStates) {
        elements = new HashSet<>(elementStates);
    }

    public void add(ElementState<E> elementState) {
        Set<ElementState<E>> conflicts = new HashSet<>();
        conflicts.add(elementState);
        Set<ElementState<E>> rest = new HashSet<>();

        for (ElementState<E> e : getElements()) {
            if (e.getId().equals(elementState.getId())) {
                conflicts.add(e);
            } else {
                rest.add(e);
            }
        }

        ElementState<E> winner = Operations.select(conflicts,
                new Operations.Predicate2<ElementState<E>, ElementState<E>>() {
                    @Override
                    public boolean call(ElementState<E> first, ElementState<E> second) {
                        return first.compareTo(second) < 0;
                    }
                });
        rest.add(winner);

        this.elements = rest;
    }

    public void remove(ElementState<E> elementState) {
        add(new ElementState<>(elementState.id, true, elementState.timestamp, elementState.element));
    }

    public OURSet<E> merge(OURSet<E> anotherOURSet) {
        final Set<ElementState<E>> union = Operations.union(elements, anotherOURSet.getElements());

        // group by elements id
        final Map<UUID, Collection<ElementState<E>>> index = Operations.groupBy(union, new Operations.Mapper<ElementState<E>, UUID>() {
            @Override
            public UUID call(ElementState<E> element) {
                return element.id;
            }
        });

        //apply the merging logic
        final Map<UUID, ElementState<E>> mergeResult = Operations.mapValues(index,
                new Operations.Mapper<Collection<ElementState<E>>, ElementState<E>>() {
                    @Override
                    public ElementState<E> call(Collection<ElementState<E>> conflicts) {
                        final ElementState<E> mergedState;
                        if (conflicts.size() > 1) {
                            mergedState = Collections.max(conflicts);
                        } else {
                            mergedState = conflicts.iterator().next();
                        }
                        return mergedState;
                    }
                });

        return new OURSet<>(new HashSet<>(mergeResult.values()));
    }

    public OURSet<E> diff(OURSet<E> anotherOURSet) {
        final OURSet<E> mergeResult = merge(anotherOURSet);
        final Set<ElementState<E>> diff = Operations.diff(mergeResult.getElements(), anotherOURSet.getElements());

        return new OURSet<>(diff);
    }

    public Set<E> lookup() {
        return Operations.filteredAndMapped(elements, new Operations.Predicate<ElementState<E>>() {
            @Override
            public boolean call(ElementState<E> element) {
                return !element.removed;
            }
        }, new Operations.Mapper<ElementState<E>, E>() {
            @Override
            public E call(ElementState<E> element) {
                return element.element;
            }
        });
    }

    public Set<ElementState<E>> getElements() {
        return Collections.unmodifiableSet(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OURSet<?> ourSet = (OURSet<?>) o;

        return elements.equals(ourSet.elements);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }
}
