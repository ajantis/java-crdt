package io.dmitryivanov.crdt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<E> resultSet = addSet.lookup().stream().filter(e -> !removeSet.lookup().contains(e)).collect(Collectors.toSet());
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
        return firstSet.stream().filter(e -> !secondSet.contains(e)).collect(Collectors.toSet());
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
