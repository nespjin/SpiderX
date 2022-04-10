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

package com.nesp.sdk.javafx.device;

public class Screen {

    public static double getCenterStateScreenPointX() {
        return getScreenWidth() / 6;
    }

    public static double getCenterStateScreenPointY() {
        return getScreenHeight() / 6;
    }

    public static double getCenterStateScreenPointX(double stateWidth) {
        return (getScreenWidth() - stateWidth) / 2;
    }

    public static double getCenterStateScreenPointY(double stateHeight) {
        return (getScreenHeight() - stateHeight) / 2;
    }

    public static double getScreenWidth() {
        return javafx.stage.Screen.getPrimary().getBounds().getMaxX();
    }

    public static double getScreenHeight() {
        return javafx.stage.Screen.getPrimary().getBounds().getMaxY();
    }

}
