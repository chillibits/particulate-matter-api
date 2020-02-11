/*
 * Copyright © 2019 - 2020 Marc Auberer. All rights reserved.
 */

package com.chillibits.particulatematterapp.tool;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

public class Tools {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String hashMD5(String stringToHash) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(stringToHash.getBytes());
            byte[] digest = messageDigest.digest();
            return DatatypeConverter.printHexBinary(digest);
        } catch (Exception ignored) {}
        return stringToHash;
    }
}
