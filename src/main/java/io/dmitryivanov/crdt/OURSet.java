package io.dmitryivanov.crdt;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Collectors;

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
        elements = Sets.newHashSet(elementStates);
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

        ElementState<E> winner = conflicts.stream().max((o1, o2) -> o1.compareTo(o2)).get();

        rest.add(winner);

        this.elements = rest;
    }

    public void remove(ElementState<E> elementState) {
        add(new ElementState<>(elementState.id, true, elementState.timestamp, elementState.element));
    }

    public OURSet<E> merge(OURSet<E> anotherOURSet) {
        final Sets.SetView<ElementState<E>> union = Sets.union(elements, anotherOURSet.getElements());

        // group by elements id
        final ImmutableMap<UUID, Collection<ElementState<E>>> index = Multimaps.index(union, ElementState::getId).asMap();

        //apply the merging logic
        final Map<UUID, ElementState<E>> mergeResult = Maps.transformEntries(index, (key, conflicts) -> {
            final ElementState<E> mergedState;

            if (conflicts.size() > 1) {
                mergedState = Collections.max(conflicts);
            } else {
                mergedState = conflicts.iterator().next();
            }

            return mergedState;
        });

        return new OURSet<>(ImmutableSet.copyOf(mergeResult.values()));
    }

    public OURSet<E> diff(OURSet<E> anotherOURSet) {
        final OURSet<E> mergeResult = merge(anotherOURSet);
        final Sets.SetView<ElementState<E>> diff = Sets.difference(mergeResult.getElements(), anotherOURSet.getElements());

        return new OURSet<>(diff);
    }

    public Set<E> lookup() {
        return elements.stream().filter(e -> !e.removed).map(e -> e.element).collect(Collectors.toSet());
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
