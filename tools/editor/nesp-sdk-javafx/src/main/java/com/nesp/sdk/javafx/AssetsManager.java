package com.nesp.sdk.javafx;

import java.io.InputStream;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/8 22:32
 * Project: JavaFx SDK
 * Description:
 **/
public class AssetsManager {

    private static final String TAG = "AssetsManager";

    private final Context mContext;

    public AssetsManager(Context context) {
        this.mContext = context;
    }

    public InputStream open(String assetPath) {
        return mContext.getContextClassLoader().getResourceAsStream("assets/" + assetPath);
    }

}
