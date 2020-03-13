package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;

public class MapsPlaceResult {
    // Constants
    private final String UNKNOWN_COUNTRY = "Unknown country";
    private final String UNKNOWN_CITY = "Unknown city";

    // Variables as objects
    public PlusCode plus_code;
    public Result[] results;
    public String status;

    public String getCountry() {
        String country = UNKNOWN_COUNTRY;
        outer_loop:
        for (Result r : results) {
            for(AddressComponent ac : r.address_components) {
                if(Arrays.asList(ac.types).contains("country")) {
                    country = ac.long_name;
                    break outer_loop;
                }
            }
        }
        return country;
    }

    public String getCity() {
        String city = UNKNOWN_CITY;
        outer_loop:
        for (Result r : results) {
            for(AddressComponent ac : r.address_components) {
                if(Arrays.asList(ac.types).contains("locality")) {
                    city = ac.long_name;
                    break outer_loop;
                }
            }
        }
        return city;
    }

    //--------------------------------------------------Sub classes-----------------------------------------------------

    @AllArgsConstructor
    @NoArgsConstructor
    private static class PlusCode {
        public String compound_code;
        public String global_code;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Result {
        public AddressComponent[] address_components;
        public String formatted_address;
        public Geometry geometry;
        public String place_id;
        public PlusCode plus_code;
        public String[] types;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class AddressComponent {
        public String long_name;
        public String short_name;
        public String[] types;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Geometry {
        public ViewPort bounds;
        public Location location;
        public String location_type;
        public ViewPort viewport;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Location {
        public String lat;
        public String lng;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class ViewPort {
        public Location northeast;
        public Location southwest;
    }
}