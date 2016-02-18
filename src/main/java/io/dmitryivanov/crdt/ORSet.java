package io.dmitryivanov.crdt;

import java.util.Set;
import java.util.stream.Collectors;

public class ORSet<E> {

    private GSet<ElementState<E>> addSet;

    private GSet<ElementState<E>> removeSet;

    public static class ElementState<E> {
        private String tag;
        private E element;

        public ElementState(String tag, E element) {
            this.tag = tag;
            this.element = element;
        }

        public String getTag() {
            return tag;
        }

        public E getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ElementState<?> that = (ElementState<?>) o;

            return !(tag != null ? !tag.equals(that.tag) : that.tag != null) && !(element != null ? !element.equals(that.element) : that.element != null);
        }

        @Override
        public int hashCode() {
            int result = tag != null ? tag.hashCode() : 0;
            result = 31 * result + (element != null ? element.hashCode() : 0);
            return result;
        }
    }

    public ORSet() {
        this.addSet = new GSet<>();
        this.removeSet = new GSet<>();
    }

    ORSet(GSet<ElementState<E>> addSet, GSet<ElementState<E>> removeSet) {
        this.addSet = addSet;
        this.removeSet = removeSet;
    }

    public void add(ElementState<E> elementState) {
        addSet.add(elementState);
    }

    public void remove(ElementState<E> elementState) {
        removeSet.add(elementState);
    }

    public ORSet<E> merge(ORSet<E> anotherORSet) {
        return new ORSet<>(addSet.merge(anotherORSet.addSet), removeSet.merge(anotherORSet.removeSet));
    }

    public ORSet<E> diff(ORSet<E> anotherORSet) {
        return new ORSet<>(addSet.diff(anotherORSet.addSet), removeSet.diff(anotherORSet.removeSet));
    }

    public Set<E> lookup() {
        return addSet.lookup().stream().filter(this::nonRemoved).map(ElementState::getElement).collect(Collectors.toSet());
    }

    private boolean nonRemoved(ElementState<E> addState) {
        Set<ElementState<E>> removes =
                removeSet.lookup().stream()
                        .filter(removeState -> removeState.getElement().equals(addState.getElement())
                                && removeState.getTag().equals(addState.getTag())).collect(Collectors.toSet());
        return removes.isEmpty();
    }

    // Visible for testing
    GSet<ElementState<E>> getAddSet() {
        return new GSet<ElementState<E>>().merge(addSet);
    }

    // Visible for testing
    GSet<ElementState<E>> getRemoveSet() {
        return new GSet<ElementState<E>>().merge(removeSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ORSet<?> orSet = (ORSet<?>) o;

        return addSet.equals(orSet.addSet) && removeSet.equals(orSet.removeSet);
    }

    @Override
    public int hashCode() {
        int result = addSet.hashCode();
        result = 31 * result + removeSet.hashCode();
        return result;
    }
}
