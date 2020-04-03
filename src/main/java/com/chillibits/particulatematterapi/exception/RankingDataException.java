/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import java.util.HashMap;

public class RankingDataException extends Exception {
    // Error description list
    private static final HashMap<Integer, String> descriptions = new HashMap<>() {{
        put(ErrorCodeUtils.INVALID_ITEMS_NUMBER, "Invalid items number. Please provide a number >= 1");
    }};

    public RankingDataException(int errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}
