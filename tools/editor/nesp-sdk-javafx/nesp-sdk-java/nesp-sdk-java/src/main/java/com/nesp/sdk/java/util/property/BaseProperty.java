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

package com.nesp.sdk.java.util.property;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/4 下午1:20
 * Description:
 **/
public class BaseProperty<T> implements Property {

    private final List<WeakReference<OnPropertyChangedListener<T>>> mOnPropertyChangedListeners;

    private T value;

    public BaseProperty() {
        mOnPropertyChangedListeners = new ArrayList<>();
    }

    public BaseProperty(final T value) {
        this();
        this.value = value;
    }

    public void set(final T value) {
        final T old = this.value;
        this.value = value;
        invalidate();
        notifyPropertyChanged(old, value);
    }

    public T get() {
        return value;
    }

    public void notifyPropertyChanged(T oldValue, T newValue) {
        for (int size = mOnPropertyChangedListeners.size(), i = size - 1; i >= 0; i--) {
            final OnPropertyChangedListener<T> tOnPropertyChangedListener = mOnPropertyChangedListeners.get(i).get();
            if (tOnPropertyChangedListener == null) {
                mOnPropertyChangedListeners.remove(i);
            } else {
                tOnPropertyChangedListener.onChanged(this, oldValue, newValue);
            }
        }
    }

    public void addOnPropertyChangedListener(final OnPropertyChangedListener<T> onPropertyChangedListener) {
        mOnPropertyChangedListeners.add(new WeakReference<>(onPropertyChangedListener));
    }

    public void removeOnPropertyChangedListener(final OnPropertyChangedListener<T> onPropertyChangedListener) {
        for (int size = mOnPropertyChangedListeners.size(), i = size - 1; i >= 0; i--) {
            if (mOnPropertyChangedListeners.get(i) == null
                    || mOnPropertyChangedListeners.get(i).get() == null
                    || mOnPropertyChangedListeners.get(i).get() == onPropertyChangedListener) {
                mOnPropertyChangedListeners.remove(i);
            }
        }
    }

    public <I> void bind(BaseProperty<I> property, Convert<I, T> convert) {
        property.addOnPropertyChangedListener((property1, oldValue, newValue) -> set(convert.convert(newValue)));
    }

    public void bind(BaseProperty<T> property) {
        property.addOnPropertyChangedListener((property1, oldValue, newValue) -> set(newValue));
    }

    public void unbind() {
        mOnPropertyChangedListeners.clear();
    }

    /**
     * Call when value set, default implements is empty
     */
    @Override
    public void invalidate() {

    }

    public interface Convert<I, O> {
        O convert(I in);
    }

}
