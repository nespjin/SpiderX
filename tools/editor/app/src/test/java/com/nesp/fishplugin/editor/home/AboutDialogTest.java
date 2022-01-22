package com.nesp.fishplugin.editor.home;

import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

class AboutDialogTest {

    @Test
    void testSystemProperties() {
        System.getProperties().forEach(new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) {
                System.out.println("key = " + o + " v = " + o2);
            }
        });
    }

}