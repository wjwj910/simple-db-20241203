package com.simpleDb;

import org.junit.jupiter.api.BeforeAll;

public class simpleDbTest {
    private static SimpleDb simpleDb;

    @BeforeAll
    public static void beforeAll() {
        simpleDb = new SimpleDb("localhost", "root", "1122", "simpleDb__test");
    }
}
