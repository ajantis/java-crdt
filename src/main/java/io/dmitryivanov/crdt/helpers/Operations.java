/*
 * Copyright (c) pakoito 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dmitryivanov.crdt.helpers;

import java.util.*;

public final class Operations {
    private Operations() {
        // No instances
    }

    public static <E> Set<E> diff(Set<E> firstSet, final Set<E> secondSet) {
        return filtered(firstSet, new Predicate<E>() {
            @Override
            public boolean call(E element) {
                return !secondSet.contains(element);
            }
        });
    }

    public static <E> Set<E> union(Set<E> firstSet, final Set<E> secondSet) {
        final Set<E> newSet = new HashSet<>();
        newSet.addAll(firstSet);
        newSet.addAll(secondSet);
        return newSet;
    }

    public static <E> Set<E> filtered(Set<E> set, Predicate<E> predicate) {
        final Set<E> newSet = new HashSet<>();
        for (E element : set) {
            if (predicate.call(element)) {
                newSet.add(element);
            }
        }
        return newSet;
    }

    public static <E, R> Set<R> filteredAndMapped(Set<E> set, Predicate<E> predicate,
            Mapper<E, R> mapper) {
        final Set<R> newSet = new HashSet<>();
        for (E element : set) {
            if (predicate.call(element)) {
                newSet.add(mapper.call(element));
            }
        }
        return newSet;
    }

    public static <E> E select(Set<E> set, Predicate2<E, E> predicate) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Empty set for select operation");
        }
        E winner = set.iterator().next();
        for (E element : set) {
            if (predicate.call(winner, element)) {
                winner = element;
            }
        }
        return winner;
    }

    public static <K, V> Map<K, Collection<V>> groupBy(Collection<V> map, Mapper<V, K> mapper) {
        final Map<K, Collection<V>> newMap = new HashMap<>(map.size());
        for (V element : map) {
            K key = mapper.call(element);
            Collection<V> list = newMap.get(key);
            if (null == list) {
                list = new ArrayList<>();
            }
            list.add(element);
            newMap.put(key, list);
        }
        return newMap;
    }

    public static <K, V, R> Map<K, R> mapValues(Map<K, V> map, Mapper<V, R> mapper) {
        final Map<K, R> newMap = new HashMap<>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            newMap.put(entry.getKey(), mapper.call(entry.getValue()));
        }
        return newMap;
    }

    public interface Predicate<E> {
        boolean call(E element);
    }

    public interface Predicate2<E, F> {
        boolean call(E first, F second);
    }

    public interface Mapper<E, R> {
        R call(E element);
    }
}
