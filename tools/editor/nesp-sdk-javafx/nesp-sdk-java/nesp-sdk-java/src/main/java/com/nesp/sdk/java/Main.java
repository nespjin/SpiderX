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
package com.nesp.sdk.java;

import com.nesp.sdk.java.ranges.IntRange;
import com.nesp.sdk.java.util.ArrayListCompat;

/**
 * The main class for jdk, write test code here.
 * <p>
 * Date 2020-9-26 2:03:16
 *
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 */
public class Main {

    public static void main(String[] args) {

        final ArrayListCompat<Student> studentArrayListCompat = new ArrayListCompat<>();
        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.age = i;
            student.name = "name" + i;
            studentArrayListCompat.add(student);
        }

        // filter sample
        println("filter sample");
        studentArrayListCompat.filter(item -> item.age > 1).forEach(t -> {
            // it will print 2 3 4 5 6 7 8 9
            System.out.println(t.age);
        });
        println("");
        // it will print name0
        studentArrayListCompat.filterIndexed((index, element) -> index < 1).forEach(item -> println(item.name));

        // map sample
        println("map sample");
        // it will print name0  name1  name2  name3 .....  name9
        studentArrayListCompat.map(item -> item.name).forEach(Main::println);

        // filter and map sample
        println("filter and filter sample");
        // it will print name2 name3
        studentArrayListCompat
                .filter((t) -> t.age > 1 && t.age < 4)
                .map(item -> item.name).forEach(Main::println);

        // slice sample
        println("slice sample");
        // it will print name0 name2 name4 name6 name8
        studentArrayListCompat.slice(new IntRange(0, studentArrayListCompat.lastIndex(), 2))
                .forEach(item -> println(item.name));

        // take sample
        println("take sample");
        // it will print name2 name3
        studentArrayListCompat.take(2).forEach(item -> println(item.name));
        println("");
        // it will print name8 nam9
        studentArrayListCompat.takeLast(2).forEach(item -> println(item.name));

        println("reversed sample");
        // it will print name9 name8 ... name0
        studentArrayListCompat.reversed().forEach(item -> println(item.name));

        println("join sample");
        // join sample
        String joinToString = studentArrayListCompat.joinToString(",", "", "", 1, "...", element -> element.name);
        println(joinToString);

        // more api usages to see java doc
    }

    /**
     * The class for test.
     */
    public static class Student {

        public int age = 0;
        public String name = "";
    }

    public static void println(Object o) {
        System.out.println(o);
    }
}
