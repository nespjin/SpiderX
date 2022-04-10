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

package com.nesp.fishplugin.fdb;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 4/11/2022 1:02 AM
 * Description:
 **/
public class Main {

    /**
     * The entry of fdb
     *
     * <p>
     *     <b>Usage:</b>
     *     fdb pm install <plugin file path>
     *     fdb pm uninstall <plugin id>
     *     fdb
     *
     * </p>
     *
     * @param args the arguments for fdb command
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        if (args[0].equals("pm")) {
            if (args.length == 2) {
                if (args[1].equals("install")) {
                    System.out.println("fdb pm install <plugin file path>");
                } else if (args[1].equals("uninstall")) {
                    System.out.println("fdb pm uninstall <plugin id>");
                }
            } else if (args.length == 3) {
                if (args[1].equals("install")) {
                    FdbClient.install(args[2]);
                } else if (args[1].equals("uninstall")) {
                    FdbClient.uninstall(args[2]);
                }
            }
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("fdb pm install <plugin file path>");
        System.out.println("fdb pm uninstall <plugin id>");
        System.out.println("fdb");
    }


}
