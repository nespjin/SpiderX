package com.nesp.fishplugin.editor.plugin;

import com.nesp.sdk.java.util.OnResultListener;
import javafx.animation.Transition;
import javafx.util.Duration;

public class MoviePluginBuilder implements PluginBuilder {

    @Override
    public String[] getBuildTypes() {
        return new String[]{
                "Build",
                "Test Home Page:DSL",
                "Test Home Page:JS",
                "Test Category Page:DSL",
                "Test Category Page:JS",
                "Test Search Page:DSL",
                "Test Search Page:JS",
                "Test Detail Page:DSL",
                "Test Detail Page:JS",
        };
    }

    @Override
    public void build(int buildType, OnBuildProgressListener onBuildProgressListener) {
        String buildTypeString = getBuildTypes()[buildType];
        if (onBuildProgressListener != null) {
            onBuildProgressListener.onProgress(-1, 0, "Starting " + buildTypeString);
        }



        switch (buildType) {
            case 0:
                // build
                if (onBuildProgressListener != null) {
                    Transition transition = new Transition() {
                        {
                            setCycleDuration(Duration.seconds(10));
                        }

                        @Override
                        protected void interpolate(double frac) {

                        }
                    };
                    transition.play();
                }
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
        }
    }

    @Override
    public void stopBuild(OnResultListener<Boolean> onResultListener) {
        onResultListener.onResult(true);
    }
}
