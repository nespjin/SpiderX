package com.nesp.sdk.javafx.content;

import java.util.HashSet;
import java.util.Set;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/1 下午3:50
 * Description:
 **/
public final class IntentFilter {

    private final Set<String> mActions;

    public IntentFilter(final String action) {
        this.mActions = new HashSet<>();
        addAction(action);
    }

    public IntentFilter addAction(final String action) {
        this.mActions.add(action);
        return this;
    }

    public Set<String> getActions() {
        return this.mActions;
    }
}
