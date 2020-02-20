/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.model;

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

    private static class PlusCode {
        public String compound_code;
        public String global_code;

        public PlusCode() {}
        public PlusCode(String compound_code, String global_code) {
            this.compound_code = compound_code;
            this.global_code = global_code;
        }
    }

    private static class Result {
        public AddressComponent[] address_components;
        public String formatted_address;
        public Geometry geometry;
        public String place_id;
        public PlusCode plus_code;
        public String[] types;

        public Result() {}
        public Result(AddressComponent[] address_components, String formatted_address, Geometry geometry, String place_id, PlusCode plus_code, String[] types) {
            this.address_components = address_components;
            this.formatted_address = formatted_address;
            this.geometry = geometry;
            this.place_id = place_id;
            this.plus_code = plus_code;
            this.types = types;
        }
    }

    private static class AddressComponent {
        public String long_name;
        public String short_name;
        public String[] types;

        public AddressComponent() {}
        public AddressComponent(String long_name, String short_name, String[] types) {
            this.long_name = long_name;
            this.short_name = short_name;
            this.types = types;
        }
    }

    private static class Geometry {
        public ViewPort bounds;
        public Location location;
        public String location_type;
        public ViewPort viewport;

        public Geometry() {}
        public Geometry(Location location, String location_type, ViewPort viewport) {
            this.location = location;
            this.location_type = location_type;
            this.viewport = viewport;
        }
    }

    private static class Location {
        public String lat;
        public String lng;

        public Location() {}
        public Location(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    private static class ViewPort {
        public Location northeast;
        public Location southwest;

        public ViewPort() {}
        public ViewPort(Location northeast, Location southwest) {
            this.northeast = northeast;
            this.southwest = southwest;
        }
    }
}