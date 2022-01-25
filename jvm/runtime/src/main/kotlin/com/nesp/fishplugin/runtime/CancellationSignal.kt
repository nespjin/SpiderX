package com.nesp.fishplugin.runtime

/**
 * Creates a cancellation signal, initially not canceled.
 */
class CancellationSignal {
    private val lock = Object()
    private var mIsCanceled = false
    private var mOnCancelListener: OnCancelListener? = null
    private var mCancelInProgress = false

    /**
     * Returns true if the operation has been canceled.
     *
     * @return True if the operation has been canceled.
     */
    val isCanceled: Boolean
        get() {
            synchronized(lock) { return mIsCanceled }
        }

    /**
     * Throws [OperationCanceledException] if the operation has been canceled.
     *
     * @throws OperationCanceledException if the operation has been canceled.
     */
    fun throwIfCanceled() {
        if (isCanceled) {
            throw OperationCanceledException()
        }
    }

    /**
     * Cancels the operation and signals the cancellation listener.
     * If the operation has not yet started, then it will be canceled as soon as it does.
     */
    fun cancel() {
        val listener: OnCancelListener?
        synchronized(lock) {
            if (mIsCanceled) {
                return
            }
            mIsCanceled = true
            mCancelInProgress = true
            listener = mOnCancelListener
        }
        try {
            listener?.onCancel()
        } finally {
            synchronized(lock) {
                mCancelInProgress = false
                lock.notifyAll();
            }
        }
    }

    /**
     * Sets the cancellation listener to be called when canceled.
     *
     *
     * This method is intended to be used by the recipient of a cancellation signal
     * such as a database or a content provider to handle cancellation requests
     * while performing a long-running operation.  This method is not intended to be
     * used by applications themselves.
     *
     *
     * If [CancellationSignal.cancel] has already been called, then the provided
     * listener is invoked immediately.
     *
     *
     * This method is guaranteed that the listener will not be called after it
     * has been removed.
     *
     * @param listener The cancellation listener, or null to remove the current listener.
     */
    fun setOnCancelListener(listener: OnCancelListener?) {
        synchronized(lock) {
            waitForCancelFinishedLocked()
            if (mOnCancelListener === listener) {
                return
            }
            mOnCancelListener = listener
            if (!mIsCanceled || listener == null) {
                return
            }
        }
        listener!!.onCancel()
    }

    private fun waitForCancelFinishedLocked() {
        while (mCancelInProgress) {
            try {
                lock.wait()
            } catch (ignored: InterruptedException) {
            }
        }
    }

    /**
     * Listens for cancellation.
     */
    interface OnCancelListener {
        /**
         * Called when [CancellationSignal.cancel] is invoked.
         */
        fun onCancel()
    }
}