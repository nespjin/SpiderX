package com.nesp.sdk.java.util;

public interface CancelableRunnable extends Runnable, Cancelable {

    void cancel();

    boolean isCancel();

    boolean isFinished();

}
