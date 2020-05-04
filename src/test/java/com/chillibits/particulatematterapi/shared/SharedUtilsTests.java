/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.shared;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class SharedUtilsTests {
    @Test
    public void testRound() {
        double result = SharedUtils.round(1.12345, 2);
        assertEquals(1.12, result);

        result = SharedUtils.round(2.00001, 0);
        assertEquals(2, result);
    }

    @Test
    public void testRoundNegativePlaces() {
        // Try with invalid input
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                SharedUtils.round(1.12345, -2)
        );

        String expectedMessage = new IllegalArgumentException().getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }
}