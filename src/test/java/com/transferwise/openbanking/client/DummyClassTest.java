package com.transferwise.openbanking.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DummyClassTest {

    private final DummyClass dummyClass = new DummyClass();

    @Test
    void getHelloWorld() {
        Assertions.assertEquals("Hello World!", dummyClass.getHelloWorld());
    }
}
