/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.shared;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SharedUtilsTests {
    @Test
    public void testRound() {
        double result = SharedUtils.round(1.12345, 2);
        assertEquals(1.12, result);

        result = SharedUtils.round(2.00001, 0);
        assertEquals(2, result);
    }
}