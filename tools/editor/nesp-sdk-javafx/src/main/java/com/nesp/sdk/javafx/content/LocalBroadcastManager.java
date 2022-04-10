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

package com.nesp.sdk.javafx.content;

import com.nesp.sdk.java.annotation.NonNull;
import javafx.application.Platform;

import java.util.*;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/1 下午3:46
 * Description:
 **/
public final class LocalBroadcastManager {

    private static volatile LocalBroadcastManager sInstance;

    private final WeakHashMap<BroadcastReceiver, IntentFilter> mRegisterData;

    private final Queue<Intent> mSentIntentQueue;
    private final DispatchIntentRunnable mDispatchIntentRunnable;

    public static LocalBroadcastManager getInstance() {
        if (sInstance == null) {
            synchronized (LocalBroadcastManager.class) {
                if (sInstance == null) {
                    sInstance = new LocalBroadcastManager();
                }
            }
        }
        return sInstance;
    }

    private LocalBroadcastManager() {
        mRegisterData = new WeakHashMap<>();
        mSentIntentQueue = new LinkedList<>();
        mDispatchIntentRunnable = new DispatchIntentRunnable(mSentIntentQueue, mRegisterData);
    }

    public void sendBroadcast(@NonNull Intent intent) {
        if (mRegisterData.isEmpty()) {
            return;
        }
        mSentIntentQueue.add(intent);
        runDispatchIntent();
    }

    private void runDispatchIntent() {
        if (!mDispatchIntentRunnable.isRunning()) {
            final Thread thread = new Thread(mDispatchIntentRunnable);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void registerReceiver(@NonNull BroadcastReceiver receiver,
                                 @NonNull IntentFilter filter) {
        mRegisterData.putIfAbsent(receiver, filter);
    }

    public void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
        mRegisterData.remove(receiver);
    }

    private static class DispatchIntentRunnable implements Runnable {

        private final Queue<Intent> mIntentQueue;
        private final Map<BroadcastReceiver, IntentFilter> mRegisterData;
        private boolean mIsRunning;

        public DispatchIntentRunnable(final Queue<Intent> intentQueue,
                                      final Map<BroadcastReceiver, IntentFilter> registerData) {
            mIntentQueue = intentQueue;
            mRegisterData = registerData;
        }

        @Override
        public void run() {
            setRunning(true);
            while (!mIntentQueue.isEmpty()) {
                final Intent intent = mIntentQueue.poll();
                if (intent != null) {
                    final Set<Map.Entry<BroadcastReceiver, IntentFilter>> entries = mRegisterData.entrySet();
                    for (final Map.Entry<BroadcastReceiver, IntentFilter> entry : entries) {
                        final BroadcastReceiver receiver = entry.getKey();
                        final IntentFilter intentFilter = entry.getValue();
                        if (intentFilter.getActions().contains(intent.getAction())) {
                            Platform.runLater(() -> receiver.onReceive(intent));
                        }
                    }
                }
            }
            setRunning(false);
        }

        private void setRunning(final boolean running) {
            mIsRunning = running;
        }

        public boolean isRunning() {
            return mIsRunning;
        }
    }

}
