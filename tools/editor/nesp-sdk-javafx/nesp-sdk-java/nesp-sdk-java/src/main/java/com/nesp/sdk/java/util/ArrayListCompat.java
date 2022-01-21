/*
 * Copyright (C) 2021 The NESP Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesp.sdk.java.util;

import com.nesp.sdk.java.annotation.NonNull;
import com.nesp.sdk.java.annotation.Nullable;
import com.nesp.sdk.java.ranges.IntRange;
import com.nesp.sdk.java.ranges.RangeUtil;

import java.io.IOException;
import java.util.*;

/**
 * This class is member of java sdk collections.
 * <p>
 * Implements some advanced functions. It is unsynchronized
 * <p>
 * Examples:
 * <pre>
 *
 *
 *
 *  public class Main {
 *
 *     public static void main(String[] args) {
 *
 *         final ArrayListCompat<{@link com.nesp.sdk.java.Main.Student}> studentArrayListCompat = new ArrayListCompat<>();
 *         for (int i = 0; i < 10; i++) {
 *             Student student = new Student();
 *             student.age = i;
 *             student.name = "name" + i;
 *             studentArrayListCompat.add(student);
 *         }
 *
 *         // filter sample
 *         println("filter sample");
 *         studentArrayListCompat.filter(item -> item.age > 1).forEach(t -> {
 *             // it will print 2 3 4 5 6 7 8 9
 *             System.out.println(t.age);
 *         });
 *         println("");
 *         // it will print name0
 *         studentArrayListCompat.filterIndexed((index, element) -> index < 1).forEach(item -> println(item.name));
 *
 *         // map sample
 *         println("map sample");
 *         // it will print name0  name1  name2  name3 .....  name9
 *         studentArrayListCompat.map(item -> item.name).forEach(Main::println);
 *
 *         // filter and map sample
 *         println("filter and filter sample");
 *         // it will print name2 name3
 *         studentArrayListCompat
 *                 .filter((t) -> t.age > 1 && t.age < 4)
 *                 .map(item -> item.name).forEach(Main::println);
 *
 *         // slice sample
 *         println("slice sample");
 *         // it will print name0 name2 name4 name6 name8
 *         studentArrayListCompat.slice(new IntRange(0, studentArrayListCompat.lastIndex(), 2))
 *                 .forEach(item -> println(item.name));
 *
 *         // take sample
 *         println("take sample");
 *         // it will print name2 name3
 *         studentArrayListCompat.take(2).forEach(item -> println(item.name));
 *         println("");
 *         // it will print name8 nam9
 *         studentArrayListCompat.takeLast(2).forEach(item -> println(item.name));
 *
 *         println("reversed sample");
 *         // it will print name9 name8 ... name0
 *         studentArrayListCompat.reversed().forEach(item -> println(item.name));
 *
 *         println("join sample");
 *         // join sample
 *         String joinToString = studentArrayListCompat.joinToString(",", "", "", 1, "...", element -> element.name);
 *         println(joinToString);
 *
 *         // more api usages to see java doc
 *     }
 *
 *
 *
 *  </pre>
 * <p>
 * Date 2020-9-26 2:13:34
 *
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 */
public class ArrayListCompat<E> extends ArrayList<E> implements ListCompat<E> {

    public ArrayListCompat() {
        super();
    }

    public ArrayListCompat(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListCompat(Collection<? extends E> c) {
        super(c);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NonNull
    @Override
    public E component1() throws IndexOutOfBoundsException {
        return get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NonNull
    @Override
    public E component2() throws IndexOutOfBoundsException {
        return get(1);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NonNull
    @Override
    public E component3() throws IndexOutOfBoundsException {
        return get(2);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NonNull
    @Override
    public E component4() throws IndexOutOfBoundsException {
        return get(3);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @NonNull
    @Override
    public E component5() throws IndexOutOfBoundsException {
        return get(4);
    }

    /**
     * {@inheritDoc}
     *
     * @param index        of element at list.
     * @param defaultValue reference {@link DefaultValue}
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public E getOrElse(int index, DefaultValue<E> defaultValue) {
        if (index > 0 && index <= lastIndex()) {
            return get(index);
        }
        return defaultValue.defaultValue(index);
    }

    /**
     * {@inheritDoc}
     *
     * @param index given
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E getOrNull(int index) {
        if (index > 0 && index <= lastIndex()) {
            return get(index);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E find(Predicate<E> predicate) {
        return null;
    }

    @Nullable
    @Override
    public E findLast(Predicate<E> predicate) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int lastIndex() {
        return size() - 1;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @NonNull
    @Override
    public E first() {
        if (isEmpty()) throw new NoSuchElementException("List is empty.");
        return get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @NonNull
    @Override
    public E first(Predicate<E> predicate) throws NoSuchElementException {
        for (E element : this) if (predicate.predicate(element)) return element;
        throw new NoSuchElementException("List contains no element matching the predicate.");
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E firstOrNull() {
        if (isEmpty()) return null;
        return get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E firstOrNull(Predicate<E> predicate) {
        if (isEmpty()) return null;
        return get(0);
    }

    /**
     * @param index to check.
     * @return {@inheritDoc}
     * Checks list is empty or not at first, if list is empty return false.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean checkIndexOverflow(int index) {
        if (isEmpty()) return false;
        return index >= 0 && index < size();
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public int indexOfFirst(Predicate<E> predicate) {
        int index = 0;
        for (E element : this) {
            if (predicate.predicate(element)) return index;
            index++;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public int indexOfLast(Predicate<E> predicate) {
        ListIterator<E> listIterator = listIterator(size());
        while (listIterator.hasPrevious()) {
            if (predicate.predicate(listIterator.previous())) {
                return listIterator.nextIndex();
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public E last() {
        if (isEmpty()) throw new NoSuchElementException("Collection is empty.");
        return get(lastIndex());
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @NonNull
    @Override
    public E last(Predicate<E> predicate) throws NoSuchElementException {
        ListIterator<E> listIterator = listIterator(size());
        while (listIterator.hasPrevious()) {
            E element = listIterator.previous();
            if (predicate.predicate(element)) return element;
        }
        throw new NoSuchElementException("List contains no element matching the" +
                " predicate.");
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E lastOrNull() {
        if (isEmpty()) return null;
        return get(lastIndex());
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E lastOrNull(Predicate<E> predicate) {
        ListIterator<E> iterator = this.listIterator(size());
        while (iterator.hasPrevious()) {
            E element = iterator.previous();
            if (predicate.predicate(element)) return element;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public E random() {
        return random(new Random());
    }

    /**
     * {@inheritDoc}
     *
     * @param random specified
     * @return {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @NonNull
    @Override
    public E random(Random random) throws NoSuchElementException {
        if (isEmpty()) throw new NoSuchElementException("List is empty.");
        return get(random.nextInt(size()));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E randomOrNull() {
        return randomOrNull(new Random());
    }

    /**
     * {@inheritDoc}
     *
     * @param random specified
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E randomOrNull(Random random) {
        if (isEmpty()) return null;
        return get(random.nextInt(size()));
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws NoSuchElementException   {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull
    @Override
    public E single() throws NoSuchElementException, IllegalArgumentException {
        switch (size()) {
            case 0:
                throw new NoSuchElementException("List is empty.");
            case 1:
                return get(0);
            default:
                throw new IllegalArgumentException("List has more than one element.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     * @throws NoSuchElementException   {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @NonNull
    @Override
    public E single(Predicate<E> predicate) throws NoSuchElementException, IllegalArgumentException {
        E single = null;
        boolean found = false;
        for (E element : this) {
            if (predicate.predicate(element)) {
                if (found) throw new IllegalArgumentException("List contains more than one " +
                        "matching element.");
                single = element;
                found = true;
            }
        }
        if (!found) throw new NoSuchElementException("List contains no element matching the " +
                "predicate.");
        return single;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E singleOrNull() {
        return size() == 1 ? get(0) : null;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public E singleOrNull(Predicate<E> predicate) {
        E single = null;
        boolean found = false;
        for (E element : this) {
            if (predicate.predicate(element)) {
                if (found) return null;
                single = element;
                found = true;
            }
        }
        if (!found) return null;
        return single;
    }

    /**
     * {@inheritDoc}
     *
     * @param n the number of element to exclude.
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ListCompat<E> drop(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Requested element count " + n + " is less than " +
                    "zero.");
        }
        ArrayListCompat<E> arrayListCompat;
        final int resultSize = size() - n;
        if (resultSize <= 0) return new ArrayListCompat<>();
        if (resultSize == 1) return listOf(last());
        arrayListCompat = new ArrayListCompat<>(resultSize);
        for (int index = n; index < size(); index++) {
            arrayListCompat.add(get(index));
        }
        return arrayListCompat;
    }

    /**
     * {@inheritDoc}
     *
     * @param n the number of element to exclude.
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ListCompat<E> dropLast(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Requested element count " + n + " is less than " +
                    "zero.");
        }
        return take(RangeUtil.coerceAtLeast(size() - n, 0));
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> dropLastWhile(Predicate<E> predicate) {
        if (isNotEmpty()) {
            ListIterator<E> listIterator = listIterator(size());
            while (listIterator.hasPrevious()) {
                if (!predicate.predicate(listIterator.previous())) {
                    return take(listIterator.nextIndex() + 1);
                }
            }
        }
        return new ArrayListCompat<>();
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate {@link }
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> dropWhile(Predicate<E> predicate) {
        boolean yielding = false;
        ArrayListCompat<E> list = new ArrayListCompat<>();
        for (E element : this) {
            if (yielding) {
                list.add(element);
            } else if (!predicate.predicate(element)) {
                list.add(element);
                yielding = true;
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> filter(Predicate<E> predicate) {
        return filterTo(new ArrayListCompat<>(), predicate);
    }

    /**
     * {@inheritDoc}
     *
     * @param predicateIndexed reference {@link PredicateIndexed},function that takes the index
     *                         of an element and the element itself
     *                         and returns the result of predicate evaluation on the element.
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> filterIndexed(PredicateIndexed<E> predicateIndexed) {
        return filterIndexedTo(new ArrayListCompat<>(), predicateIndexed);
    }

    /**
     * {@inheritDoc}
     *
     * @param destination      list of contains results
     * @param predicateIndexed reference {@link PredicateIndexed},function that takes the index
     *                         of an element and the element itself
     *                         and returns the result of predicate evaluation on the element.
     * @param <C>              type of list to returns.
     * @return {@inheritDoc}
     */
    @Override
    public <C extends ListCompat<E>> C filterIndexedTo(C destination, PredicateIndexed<E> predicateIndexed) {
        forEach(element -> {
            int index = indexOf(element);
            if (predicateIndexed.predicateIndexed(index, element)) destination.add(element);
        });
        return destination;
    }

    /**
     * {@inheritDoc}
     *
     * @param <R>    type of elements in list to returns.
     * @param rClass class for type of returns.
     * @return {@inheritDoc}
     */
    @Override
    public <R extends E> ListCompat<R> filterIsInstance(Class<R> rClass) {
        return filterIsInstanceTo(rClass, new ArrayListCompat<>());
    }

    /**
     * {@inheritDoc}
     *
     * @param destination list of destination.
     * @param <R>         type of elements in list to returns.
     * @param <C>         type of list to returns.
     * @param rClass      class for type of returns.
     * @return {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R extends E, C extends ListCompat<R>> C filterIsInstanceTo(Class<R> rClass, C destination) {
        for (E element : this) {
            if (element.getClass().equals(rClass)) {
                destination.add((R) element);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> filterNot(Predicate<E> predicate) {
        return filterNotTo(new ArrayListCompat<>(), predicate);
    }

    /**
     * {@inheritDoc}
     *
     * @param <C>         type of elements of list to returns.
     * @param destination list of destination.
     * @param predicate   reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public <C extends ListCompat<E>> C filterNotTo(C destination, Predicate<E> predicate) {
        for (E element : this) if (!predicate.predicate(element)) destination.add(element);
        return destination;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> filterNotNull() {
        return filterNotNullTo(new ArrayListCompat<>());
    }

    /**
     * {@inheritDoc}
     *
     * @param destination list of destination.
     * @param <C>         type of list to returns.
     * @return {@inheritDoc}
     */
    @Override
    public <C extends ListCompat<E>> C filterNotNullTo(C destination) {
        for (E element : this) if (element != null) destination.add(element);
        return destination;
    }

    /**
     * {@inheritDoc}
     *
     * @param destination list of destination.
     * @param <C>         type of list to returns.
     * @param predicate   reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public <C extends ListCompat<E>> C filterTo(C destination, Predicate<E> predicate) {
        for (E element : this) if (predicate.predicate(element)) destination.add(element);
        return destination;
    }

    /**
     * {@inheritDoc}
     *
     * @param mapper function that takes the index of an element and the element
     *               itself and returns the result of the transform applied to the element.
     * @param <R>    {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <R> ListCompat<R> map(Mapper<R, E> mapper) {
        ArrayListCompat<R> rArrayListCompat = new ArrayListCompat<>();
        for (E item : this) rArrayListCompat.add(mapper.map(item));
        return rArrayListCompat;
    }

    /**
     * {@inheritDoc}
     *
     * @param indices {@link Integer} range specified.
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> slice(IntRange indices) {
        if (indices.isEmpty() || indices.start < 0 || indices.endInclusive >= size()) {
            return new ArrayListCompat<>();
        }
        ArrayListCompat<E> list = new ArrayListCompat<>();
        for (int index = indices.start; index <= indices.endInclusive; index += indices.step) {
            list.add(get(index));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @param n the number of elements.
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ListCompat<E> take(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Requested element count " + n + " is less than " +
                    "zero.");
        }
        if (n == 0) return new ArrayListCompat<>();
        if (n >= size()) return this;

        if (n == 1) return listOf(first());
        int count = 0;
        ArrayListCompat<E> list = new ArrayListCompat<>();
        for (E element : this) {
            list.add(element);
            if (++count == n) break;
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @param n the number of elements.
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ListCompat<E> takeLast(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Requested element count " + n + " is less than " +
                    "zero.");
        }
        if (n == 0) return new ArrayListCompat<>();
        int size = size();
        if (n >= size) return this;
        if (n == 1) return listOf(last());
        ArrayListCompat<E> list = new ArrayListCompat<>();
        for (int index = size - n; index < size; index++) {
            list.add(get(index));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> takeLastWhile(Predicate<E> predicate) {
        if (isEmpty()) return new ArrayListCompat<>();
        ListIterator<E> listIterator = listIterator(size());
        while (listIterator.hasPrevious()) {
            if (!predicate.predicate(listIterator.previous())) {
                listIterator.next();
                int expectedSize = size() - listIterator.nextIndex();
                if (expectedSize == 0) new ArrayListCompat<>();
                ArrayListCompat<E> list = new ArrayListCompat<>();
                while (listIterator.hasNext()) list.add(listIterator.next());
                return list;
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param predicate reference {@link Predicate}
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> takeWhile(Predicate<E> predicate) {
        ArrayListCompat<E> list = new ArrayListCompat<>();
        for (E element : this) {
            if (!predicate.predicate(element)) break;
            list.add(element);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reverse() {
        Collections.reverse(this);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public ListCompat<E> reversed() {
        ArrayListCompat<E> list = new ArrayListCompat<>();
        list.addAll(this);
        Collections.reverse(list);
        return list;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void shuffle() {
        Collections.shuffle(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param random as the source of randomness
     */
    @Override
    public void shuffle(Random random) {
        Collections.shuffle(this, random);
    }

    /**
     * {@inheritDoc}
     *
     * @param i the index of one element to be swapped.
     * @param j the index of the other element to be swapped.
     * @throws IndexOutOfBoundsException if either {@code i} or {@code j}
     *                                   is out of range (i &lt; 0 || i &gt;= list.size()
     *                                   || j &lt; 0 || j &gt;= list.size()).
     */
    @Override
    public void swap(int i, int j) throws IndexOutOfBoundsException {
        Collections.swap(this, i, j);
    }

    /**
     * {@inheritDoc}
     *
     * @param buffer    destination
     * @param separator separated using separator
     * @param prefix    of string for returns.
     * @param postfix   of string for returns.
     * @param limit     appends first limit elements
     * @param truncated followed by the truncated string.
     * @param <A>                   {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public <A extends Appendable> A joinTo(A buffer, CharSequence separator, CharSequence prefix,
                                           CharSequence postfix, int limit,
                                           CharSequence truncated,
                                           CharSequenceTransform<E> charSequenceTransform) {
        try {
            buffer.append(prefix);
            int count = 0;
            for (E element : this) {
                if (++count > 1) buffer.append(separator);
                if (limit < 0 || count <= limit) {
                    buffer.append(charSequenceTransform.transform(element));
                } else break;
            }
            if (limit > 0 && count > limit) buffer.append(truncated);
            buffer.append(postfix);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param buffer                destination
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @param <A>                   {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public <A extends Appendable> A joinTo(A buffer,
                                           CharSequenceTransform<E> charSequenceTransform) {
        return joinTo(buffer, ",", "", "", -1, "...", charSequenceTransform);
    }

    /**
     * {@inheritDoc}
     *
     * @param buffer                destination
     * @param separator             separated using separator
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @param <A>                   {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public <A extends Appendable> A joinTo(A buffer, CharSequence separator,
                                           CharSequenceTransform<E> charSequenceTransform) {
        return joinTo(buffer, separator, "", "", -1, "...", charSequenceTransform);
    }

    /**
     * @param separator             separated using separator
     * @param prefix                of string for returns.
     * @param postfix               of string for returns.
     * @param limit                 appends first limit elements
     * @param truncated             followed by the truncated string.
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public String joinToString(CharSequence separator, CharSequence prefix, CharSequence postfix,
                               int limit, CharSequence truncated,
                               CharSequenceTransform<E> charSequenceTransform) {
        StringBuilder stringBuilder = joinTo(new StringBuilder(), separator, prefix, postfix, limit, truncated,
                charSequenceTransform);
        if (stringBuilder == null) return "";
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public String joinToString(CharSequenceTransform<E> charSequenceTransform) {
        return joinToString(",", "", "", -1, "...", charSequenceTransform);
    }

    /**
     * {@inheritDoc}
     *
     * @param separator             separated using separator
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return {@inheritDoc}
     */
    @NonNull
    @Override
    public String joinToString(CharSequence separator, CharSequenceTransform<E> charSequenceTransform) {
        return joinToString(separator, "", "", -1, "...", charSequenceTransform);
    }

    @SafeVarargs
    public static <E> ArrayListCompat<E> listOf(@NonNull E... elements) {
        ArrayListCompat<E> arrayListCompat = new ArrayListCompat<>();
        if (elements.length > 0) {
            for (E element : elements) if (element != null) arrayListCompat.add(element);
        }
        return arrayListCompat;
    }

}
