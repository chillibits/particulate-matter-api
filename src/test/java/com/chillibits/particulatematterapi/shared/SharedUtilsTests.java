/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
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
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGenerateRandomStringLength20() {
        String result = SharedUtils.generateRandomString(20);
        assertEquals(20, result.length());
        assertTrue(result.matches("^[a-zA-Z0-9]+$"));
    }

    @Test
    public void testGenerateRandomStringLength10000() {
        String result = SharedUtils.generateRandomString(10000);
        assertEquals(10000, result.length());
        assertTrue(result.matches("^[a-zA-Z0-9]+$"));
    }
}