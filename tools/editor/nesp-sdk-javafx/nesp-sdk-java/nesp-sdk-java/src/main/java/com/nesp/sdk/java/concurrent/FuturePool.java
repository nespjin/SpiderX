/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.concurrent;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.UnaryOperator;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/7 2:01
 * Description:
 **/
public class FuturePool<R> implements List<Future<R>> {

    private static final String TAG = "FuturePool";

    private final ArrayList<Future<R>> mFutures = new ArrayList<>();

    @Override
    public int size() {
        return mFutures.size();
    }

    @Override
    public boolean isEmpty() {
        return mFutures.isEmpty();
    }

    @Override
    public boolean contains(@Nullable final Object o) {
        return mFutures.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Future<R>> iterator() {
        return mFutures.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return mFutures.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull final T[] a) {
        return mFutures.toArray(a);
    }

    @Override
    public boolean add(final Future<R> rFuture) {
        return mFutures.add(rFuture);
    }

    @Override
    public boolean remove(@Nullable final Object o) {
        return mFutures.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> c) {
        return mFutures.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends Future<R>> c) {
        return mFutures.addAll(c);
    }

    @Override
    public boolean addAll(final int index, @NotNull final Collection<? extends Future<R>> c) {
        return mFutures.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> c) {
        return mFutures.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> c) {
        return mFutures.retainAll(c);
    }

    @Override
    public void replaceAll(@NotNull final UnaryOperator<Future<R>> operator) {
        mFutures.replaceAll(operator);
    }

    @Override
    public void sort(@Nullable final Comparator<? super Future<R>> c) {
        mFutures.sort(c);
    }

    @Override
    public void clear() {
        /* before clear pool, we should to cancel all futures */
        cancelAll();
        mFutures.clear();
    }

    @Override
    public Future<R> get(final int index) {
        return mFutures.get(index);
    }

    @Override
    public Future<R> set(final int index, final Future<R> element) {
        return mFutures.set(index, element);
    }

    @Override
    public void add(final int index, final Future<R> element) {
        mFutures.add(index, element);
    }

    @Override
    public Future<R> remove(final int index) {
        return mFutures.remove(index);
    }

    @Override
    public int indexOf(@Nullable final Object o) {
        return mFutures.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable final Object o) {
        return mFutures.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Future<R>> listIterator() {
        return mFutures.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Future<R>> listIterator(final int index) {
        return mFutures.listIterator(index);
    }

    @NotNull
    @Override
    public List<Future<R>> subList(final int fromIndex, final int toIndex) {
        return mFutures.subList(fromIndex, toIndex);
    }

    @NotNull
    @Override
    public Spliterator<Future<R>> spliterator() {
        return mFutures.spliterator();
    }

    // Start future pool customize api

    /**
     * Cancel all executors may be executing.
     *
     * @return {@code false} if one of {@link #mFutures} if the task could not be cancelled,
     * typically because it has already completed normally;
     */
    public boolean cancelAll() {
        for (final Future<R> future : mFutures) {
            if (future.isDone() || future.isCancelled()) continue;
            /* not allow task to be completed. */
            if (!future.cancel(true)) return false;
        }
        return true;
    }

}