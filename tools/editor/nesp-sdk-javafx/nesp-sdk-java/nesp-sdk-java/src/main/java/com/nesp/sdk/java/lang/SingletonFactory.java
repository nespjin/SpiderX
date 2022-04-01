/*
 * Copyright 2021 NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.lang;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/26 上午8:56
 * Description:
 **/
@SuppressWarnings({"unchecked", "unused"})
public final class SingletonFactory {

    private static final Map<String, Object> instances = new ConcurrentHashMap<>();

    private static final Map<String, WeakReference<Object>> weakReferenceInstances = new WeakHashMap<>();

    public static void removeInstance(final Class<?> clazz) {
        instances.remove(clazz.getName());
    }

    public static <T> T getInstance(final Class<T> clazz) {
        return getInstance(clazz, null);
    }


    public static <T> T getInstance(final Class<T> clazz,
                                    final InstanceFactory<T> instanceFactory) {
        Object instance = instances.get(clazz.getName());
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                instance = instances.get(clazz.getName());
                if (instance == null) {
                    try {
                        if (instanceFactory == null) {
                            instance = clazz
                                    .getDeclaredConstructor((Class<?>) null)
                                    .newInstance();
                        } else {
                            instance = instanceFactory.instance();
                        }
                    } catch (InstantiationException | IllegalAccessException
                            | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    instances.put(clazz.getName(), instance);
                }
            }
        }
        return (T) instance;
    }

    public static void removeWeakInstance(final Class<?> clazz) {
        weakReferenceInstances.remove(clazz.getName());
    }

    public static <T> T getWeakInstance(final Class<T> clazz) {
        return getWeakInstance(clazz, null);
    }

    public static <T> T getWeakInstance(final Class<T> clazz,
                                        final InstanceFactory<T> instanceFactory) {
        WeakReference<Object> reference = weakReferenceInstances.get(clazz.getName());
        Object instance = reference == null ? null : reference.get();
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                reference = weakReferenceInstances.get(clazz.getName());
                instance = reference == null ? null : reference.get();
                if (instance == null) {
                    try {
                        if (instanceFactory == null) {
                            instance = clazz.getDeclaredConstructor().newInstance();
                        } else {
                            instance = instanceFactory.instance();
                        }
                    } catch (InstantiationException | IllegalAccessException
                            | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    weakReferenceInstances.put(clazz.getName(), new WeakReference<>(instance));
                }
            }
        }
        return (T) instance;
    }

}
