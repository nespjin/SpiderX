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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.RandomAccess;

/**
 * @param <E> the type of elements in this list
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Date 2020-9-26 13:55:43
 */
public interface ListCompat<E> extends List<E> {

    /**
     * Return 1st element of the list
     *
     * @return 1st element of the list
     * @throws IndexOutOfBoundsException if the size of this list is less than
     *                                   1.
     */
    @NonNull
    public E component1() throws IndexOutOfBoundsException;

    /**
     * Returns 2nd element of the list
     *
     * @return 2nd element of the list
     * @throws IndexOutOfBoundsException if the size of this list is less than
     *                                   2.
     */
    @NonNull
    public E component2() throws IndexOutOfBoundsException;

    /**
     * Returns 3rd element of the list
     *
     * @return 3rd element of the list
     * @throws IndexOutOfBoundsException if the size of this list is less than
     *                                   3.
     */
    @NonNull
    public E component3() throws IndexOutOfBoundsException;

    /**
     * Returns 4th element of the list
     *
     * @return 4th element of the list
     * @throws IndexOutOfBoundsException if the size of this list is less than
     *                                   4.
     */
    @NonNull
    public E component4() throws IndexOutOfBoundsException;

    /**
     * Returns 5th element of the list
     *
     * @return 5th element of the list
     * @throws IndexOutOfBoundsException if the size of this list is less than
     *                                   5.
     */
    @NonNull
    public E component5() throws IndexOutOfBoundsException;

    /**
     * Returns an element at the given index or the result of calling the
     *
     * @param index        of element at list.
     * @param defaultValue reference {@link DefaultValue}
     * @return an element at the given index or the result of calling the
     * default function if the index if out of bounds of this list.
     */
    @NonNull
    public E getOrElse(int index, DefaultValue<E> defaultValue);

    /**
     * Return an element at the given index or `null` if the index is out of
     * bounds of this list.
     *
     * @param index given
     * @return an element at the given index or `null` if the index is out of
     * bounds of this list.
     */
    @Nullable
    public E getOrNull(int index);

    /**
     * Returns {@code true} if this list contains elements.
     *
     * @return {@code true} if this list contains elements
     */
    public boolean isNotEmpty();

    /**
     * Returns the first element matching the given predicate, or `null` if no such element
     * was found.
     *
     * @param predicate reference {@link Predicate}
     * @return the first element matching the given predicate, or `null` if no such element was found.
     */
    @Nullable
    public E find(Predicate<E> predicate);

    /**
     * Rreturns the last element matching the given predicate, or `null` if no such element
     * was found.
     *
     * @param predicate reference {@link Predicate}
     * @return the last element matching the given predicate, or `null` if no such element was found.
     */
    @Nullable
    public E findLast(Predicate<E> predicate);

    /**
     * Returns the index of the last item in the list or -1 if the list is empty.
     *
     * @return the index of the last item in the list or -1 if the list is empty.
     */
    public int lastIndex();

    /**
     * Returns first element of this list.
     *
     * @return first element of this list.
     * @throws NoSuchElementException if this list is empty
     */
    @NonNull
    public E first() throws NoSuchElementException;

    /**
     * Returns the first element matching the given predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return the first element matching the given predicate.
     * @throws NoSuchElementException if no such element is found.
     */
    @NonNull
    public E first(Predicate<E> predicate) throws NoSuchElementException;

    /**
     * Returns the first element, or null if the list is empty.
     *
     * @return the first element, or null if the list is empty.
     */
    @Nullable
    public E firstOrNull();

    /**
     * Returns the first element matching the given predicate, or `null` if element was not found.
     *
     * @param predicate reference {@link Predicate}
     * @return the first element matching the given predicate, or `null` if element was not found.
     */
    @Nullable
    public E firstOrNull(Predicate<E> predicate);

    /**
     * Checks index is overflow or not.
     *
     * @param index to check.
     * @return {@code true} if index not overflow else {@code false} if index overflow.
     */
    public boolean checkIndexOverflow(int index);

    /**
     * Returns index of the first element matching the given predicate, or -1 if the list
     * does not contain such element.
     *
     * @param predicate reference {@link Predicate}
     * @return index of the first element matching the given predicate, or -1 if the list
     * does not contain such element.
     */
    public int indexOfFirst(Predicate<E> predicate);

    /**
     * Returns index of the last element matching the given predicate, or -1 if the list
     * does not contain such element.
     *
     * @param predicate reference {@link Predicate}
     * @return index of the last element matching the given predicate, or -1 if the list
     * does not contain such element.
     */
    public int indexOfLast(Predicate<E> predicate);


    /**
     * Returns the last element.
     *
     * @return the last element.
     * @throws NoSuchElementException if the list is empty.
     */
    @NonNull
    public E last() throws NoSuchElementException;


    /**
     * Returns the last element matching the given predicate
     *
     * @param predicate reference {@link Predicate}
     * @return the last element matching the given predicate
     * @throws NoSuchElementException if no such element is found.
     */
    @NonNull
    public E last(Predicate<E> predicate) throws NoSuchElementException;

    /**
     * Returns the last element, or `null` if the list is empty.
     *
     * @return the last element, or `null` if the list is empty.
     */
    @Nullable
    public E lastOrNull();

    /**
     * Returns the last element matching the given predicate, or `null` if no such element was
     * found.
     *
     * @param predicate reference {@link Predicate}
     * @return the last element matching the given predicate, or `null` if no such element was found.
     */
    @Nullable
    public E lastOrNull(Predicate<E> predicate);

    /**
     * Returns a random element from this list.
     *
     * @return a random element from this list.
     */
    @NonNull
    public E random();

    /**
     * Returns a random element from this list using the specified source of randomness.
     *
     * @param random specified
     * @return a random element from this list using the specified source of randomness.
     * @throws NoSuchElementException if this list is empty.
     */
    @NonNull
    public E random(Random random) throws NoSuchElementException;

    /**
     * Returns a random element from this collection, or `null` if this collection is empty.
     *
     * @return a random element from this collection, or `null` if this collection is empty.
     */
    @Nullable
    public E randomOrNull();

    /**
     * Returns a random element from this list using the specified source of randomness, or
     * `null` if this list is empty.
     *
     * @param random specified
     * @return a random element from this list using the specified source of randomness, or
     * `null` if this list is empty.
     */
    @Nullable
    public E randomOrNull(Random random);

    /**
     * Returns  the single element, or throws an exception if the list is empty or has more than one
     * element.
     *
     * @return the single element, or throws an exception if the list is empty or has more than one
     * element.
     * @throws NoSuchElementException   if the list is empty.
     * @throws IllegalArgumentException if has more than one.
     */
    @NonNull
    public E single() throws NoSuchElementException, IllegalArgumentException;

    /**
     * Returns the single element matching the given predicate, or throws exception if there is
     * no or more than one matching element.
     *
     * @param predicate reference {@link Predicate}
     * @return the single element matching the given predicate, or throws exception if there is
     * no or more than one matching element.
     * @throws NoSuchElementException   if the list is empty.
     * @throws IllegalArgumentException if has more than one.
     */
    @NonNull
    public E single(Predicate<E> predicate) throws NoSuchElementException, IllegalArgumentException;

    /**
     * Returns single element, or `null` if the list is empty or has more than one element.
     *
     * @return single element, or `null` if the list is empty or has more than one element.
     */
    @Nullable
    public E singleOrNull();

    /**
     * Returns the single element matching the given [predicate], or `null` if list was not
     * found or
     * more than one element was found.
     *
     * @param predicate reference {@link Predicate}
     * @return the single element matching the given [predicate], or `null` if list was not found or
     * more than one element was found.
     */
    @Nullable
    public E singleOrNull(Predicate<E> predicate);

    /**
     * Returns a list containing all list except first [n] elements.
     *
     * @param n the number of element to exclude.
     * @return a list containing all list except first [n] elements.
     * @throws IllegalArgumentException if n is negative.
     */
    public ListCompat<E> drop(int n) throws IllegalArgumentException;

    /**
     * Returns a list containing all elements except last [n] elements.
     *
     * @param n the number of element to exclude.
     * @return a list containing all elements except last [n] elements.
     * @throws IllegalArgumentException if n is negative.
     */
    public ListCompat<E> dropLast(int n) throws IllegalArgumentException;

    /**
     * Returns a list containing all elements except last elements that satisfy the given
     * predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return a list containing all elements except last elements that satisfy the given
     * predicate.
     */
    public ListCompat<E> dropLastWhile(Predicate<E> predicate);

    /**
     * Returns a list containing all elements except first elements that satisfy the given
     * predicate.
     *
     * @param predicate {@link }
     * @return a list containing all elements except first elements that satisfy the given predicate.
     */
    public ListCompat<E> dropWhile(Predicate<E> predicate);

    /**
     * Returns a list containing only elements matching the given predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return a list containing only elements matching the given predicate.
     */
    public ListCompat<E> filter(Predicate<E> predicate);

    /**
     * Returns a list containing only elements matching the given predicate.
     *
     * @param predicateIndexed reference {@link PredicateIndexed},function that takes the index
     *                         of an element and the element itself
     *                         and returns the result of predicate evaluation on the element.
     * @return a list containing only elements matching the given predicate.
     */
    public ListCompat<E> filterIndexed(PredicateIndexed<E> predicateIndexed);

    /**
     * Appends all elements matching the given predicate to the given destination.
     *
     * @param destination      list of contains results
     * @param predicateIndexed reference {@link PredicateIndexed},function that takes the index
     *                         of an element and the element itself
     *                         and returns the result of predicate evaluation on the element.
     * @param <C>              type of list to returns.
     * @return list of destination.
     */
    public <C extends ListCompat<E>> C filterIndexedTo(C destination,
                                                       PredicateIndexed<E> predicateIndexed);

    /**
     * Returns a list containing all elements that are instances of specified type parameter R.
     *
     * @param <R>    type of elements in list to returns.
     * @param rClass class for type of returns.
     * @return a list containing all elements that are instances of specified type parameter R.
     */
    public <R extends E> ListCompat<R> filterIsInstance(Class<R> rClass);

    /**
     * Appends all elements that are instances of specified type parameter R to the given
     * destination.
     *
     * @param <R>         type of elements in list to returns.
     * @param <C>         type of list to returns.
     * @param rClass      class for type of return.
     * @param destination list of destination.
     * @return list of destination.
     */
    public <R extends E, C extends ListCompat<R>> C filterIsInstanceTo(Class<R> rClass, C destination);

    /**
     * Returns a list containing all elements not matching the given predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return a list containing all elements not matching the given predicate.
     */
    public ListCompat<E> filterNot(Predicate<E> predicate);

    /**
     * Appends all elements not matching the given predicate to the given destination.
     *
     * @param <C>         type of elements of list to returns.
     * @param destination list of destination.
     * @param predicate   reference {@link Predicate}
     * @return destination.
     */
    public <C extends ListCompat<E>> C filterNotTo(C destination, Predicate<E> predicate);

    /**
     * Returns a list containing all elements that are not `null`.
     *
     * @return a list containing all elements that are not `null`.
     */
    public ListCompat<E> filterNotNull();

    /**
     * Appends all elements that are not `null` to the given destination.
     *
     * @param <C>         type of elements of list to returns.
     * @param destination list of destination.
     * @return list of destination.
     */
    public <C extends ListCompat<E>> C filterNotNullTo(C destination);

    /**
     * Appends all elements matching the given predicate to the given destination.
     *
     * @param predicate   reference {@link Predicate}
     * @param destination list of destination.
     * @param <C>         type of elements of list to returns.
     * @return list of destination.
     */
    public <C extends ListCompat<E>> C filterTo(C destination, Predicate<E> predicate);

    /**
     * Returns a list containing the results of applying the given mapper
     * function to each element and its index in the original collection.
     *
     * @param <R>    the type of elements in result list.
     * @param mapper function that takes the index of an element and the element
     *               itself and returns the result of the transform applied to the element.
     * @return a list containing the results of applying the given mapper
     * function to each element and its index in the original collection.
     */
    public <R> ListCompat<R> map(Mapper<R, E> mapper);

    /**
     * Returns a list containing elements at indices in the specified indices range.
     *
     * @param indices {@link Integer} range specified.
     * @return a list containing elements at indices in the specified indices range.
     */
    public ListCompat<E> slice(IntRange indices);

    /**
     * Returns a list containing first n elements.
     *
     * @param n the number of elements.
     * @return a list containing first n elements.
     * @throws IllegalArgumentException if n is negative.
     */
    public ListCompat<E> take(int n) throws IllegalArgumentException;

    /**
     * Returns a list containing last n elements.
     *
     * @param n the number of elements.
     * @return a list containing last n elements.
     * @throws IllegalArgumentException if n is negative.
     */
    public ListCompat<E> takeLast(int n) throws IllegalArgumentException;

    /**
     * Returns a list containing last elements satisfying the given predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return a list containing last elements satisfying the given predicate.
     */
    public ListCompat<E> takeLastWhile(Predicate<E> predicate);

    /**
     * Returns a list containing first elements satisfying the given predicate.
     *
     * @param predicate reference {@link Predicate}
     * @return a list containing first elements satisfying the given predicate.
     */
    public ListCompat<E> takeWhile(Predicate<E> predicate);

    /**
     * Reverses elements in the list in-place.
     */
    public void reverse();

    /**
     * Returns a list with elements in reversed order.
     *
     * @return a list with elements in reversed order.
     */
    public ListCompat<E> reversed();

    /**
     * Randomly permutes the specified list using a default source of
     * randomness.  All permutations occur with approximately equal
     * likelihood.
     *
     * <p>The hedge "approximately" is used in the foregoing description because
     * default source of randomness is only approximately an unbiased source
     * of independently chosen bits. If it were a perfect source of randomly
     * chosen bits, then the algorithm would choose permutations with perfect
     * uniformity.
     *
     * <p>This implementation traverses the list backwards, from the last
     * element up to the second, repeatedly swapping a randomly selected element
     * into the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     *
     * <p>This method runs in linear time.  If the specified list does not
     * implement the {@link RandomAccess} interface and is large, this
     * implementation dumps the specified list into an array before shuffling
     * it, and dumps the shuffled array back into the list.  This avoids the
     * quadratic behavior that would result from shuffling a "sequential
     * access" list in place.
     *
     * @throws UnsupportedOperationException if the specified list or
     *                                       its list-iterator does not support the {@code set}
     *                                       operation.
     */
    public void shuffle() throws UnsupportedOperationException;

    /**
     * Randomly shuffles elements in this list in-place using the specified random  instance as
     * the source of randomness.
     *
     * @param random as the source of randomness
     */
    public void shuffle(Random random);

    /**
     * Swaps the elements at the specified positions in the specified list.
     * (If the specified positions are equal, invoking this method leaves
     * the list unchanged.)
     *
     * @param i the index of one element to be swapped.
     * @param j the index of the other element to be swapped.
     * @throws IndexOutOfBoundsException if either {@code i} or {@code j}
     *                                   is out of range (i &lt; 0 || i &gt;= list.size()
     *                                   || j &lt; 0 || j &gt;= list.size()).
     */
    public void swap(int i, int j) throws IndexOutOfBoundsException;


    /**
     * Appends the string from all the elements separated using separator and using the given prefix and postfix if supplied.
     * <p>
     * If the collection could be huge, you can specify a non-negative value of limit, in which case only the first limit
     * elements will be appended, followed by the truncated string.
     *
     * @param buffer                destination
     * @param separator             separated using separator
     * @param prefix                of string for returns.
     * @param postfix               of string for returns.
     * @param limit                 appends first limit elements
     * @param truncated             followed by the truncated string.
     * @param <A>                   type of buffer
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return buffer
     */
    @Nullable
    public <A extends Appendable> A joinTo(A buffer, CharSequence separator, CharSequence prefix,
                                           CharSequence postfix, int limit,
                                           CharSequence truncated,
                                           CharSequenceTransform<E> charSequenceTransform);


    /**
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @param <A>                   type of buffer
     * @param buffer                destination
     * @return buffer.
     */
    @Nullable
    public <A extends Appendable> A joinTo(A buffer,
                                           CharSequenceTransform<E> charSequenceTransform);

    /**
     * @param separator             separated using separator
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @param <A>                   type of buffer
     * @param buffer                destination
     * @return buffer.
     */
    @Nullable
    public <A extends Appendable> A joinTo(A buffer, CharSequence separator,
                                           CharSequenceTransform<E> charSequenceTransform);

    /**
     * Creates a string from all the elements separated using separator and using the given
     * prefix and postfix if supplied.
     * <p>
     * If the collection could be huge, you can specify a non-negative value of limit, in which
     * case only the first limit elements will be appended, followed by the truncated string.
     *
     * @param separator             separated using separator
     * @param prefix                of string for returns.
     * @param postfix               of string for returns.
     * @param limit                 appends first limit elements
     * @param truncated             followed by the truncated string.
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return string
     */
    @NonNull
    public String joinToString(CharSequence separator, CharSequence prefix,
                               CharSequence postfix, int limit, CharSequence truncated,
                               CharSequenceTransform<E> charSequenceTransform);

    /**
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return string.
     */
    @NonNull
    public String joinToString(CharSequenceTransform<E> charSequenceTransform);

    /**
     * @param separator             separated using separator
     * @param charSequenceTransform reference {@link CharSequenceTransform}
     * @return string.
     */
    @NonNull
    public String joinToString(CharSequence separator,
                               CharSequenceTransform<E> charSequenceTransform);
}

