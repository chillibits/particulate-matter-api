/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Api(value = "Chart REST Endpoint", tags = "chart")
@SwaggerDefinition(tags = {
        @Tag(name = "chart", description = "Chart controller")
})
public class RoutingConfig {
    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ApiOperation(value = "Redirects the user to the swagger ui page", hidden = true)
    public String swagger() {
        return "redirect:/swagger-ui.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart")
    @ApiOperation(value = "Returns a chart of the measurements")
    public String chart(
            @RequestParam long chipId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "500") int height
    ) {
        return "chart.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart", params = "country")
    @ApiOperation(value = "Returns a chart of the measurements for a specific country")
    public String chartCountry(
            @RequestParam String country,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int period, // in minutes
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "500") int height
    ) {
        return "chart_country.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart", params = {"country", "city"})
    @ApiOperation(value = "Returns a chart of the measurements for a specific city")
    public String chartCity(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int period, // in minutes
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "500") int height
    ) {
        return "chart_city.html";
    }
}
