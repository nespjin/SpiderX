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

package com.nesp.fishplugin.installer;

public class InstallException extends Exception {

    public InstallException() {
    }

    public InstallException(String message) {
        super(message);
    }

    public InstallException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallException(Throwable cause) {
        super(cause);
    }

}
