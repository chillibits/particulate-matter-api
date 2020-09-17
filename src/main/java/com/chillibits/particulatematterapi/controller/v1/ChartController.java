/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Api(value = "Chart REST Endpoint", tags = "chart")
public class ChartController {

    private enum Types { chart, stock }
    private enum DataTypes { line, spline, area, areaspline, bar, column }

    @RequestMapping(method = RequestMethod.GET, value = "/chart")
    @ApiOperation(value = "Returns a chart of the measurements")
    public String chart(
            @RequestParam long chipId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "600") int height,
            @RequestParam(defaultValue = "chart") Types chartType,
            @RequestParam(defaultValue = "line") DataTypes type
    ) {
        return chartType == Types.chart ? "chart.html" : "chart_stock.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart", params = "country")
    @ApiOperation(value = "Returns a chart of the measurements for a specific country")
    public String chartCountry(
            @RequestParam String country,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int granularity, // in minutes
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "600") int height,
            @RequestParam(defaultValue = "chart") Types chartType,
            @RequestParam(defaultValue = "line") DataTypes type
    ) {
        return chartType == Types.chart ? "chart_country.html" : "chart_stock_country.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart", params = {"country", "city"})
    @ApiOperation(value = "Returns a chart of the measurements for a specific city")
    public String chartCity(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int granularity, // in minutes
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "600") int height,
            @RequestParam(defaultValue = "chart") Types chartType,
            @RequestParam(defaultValue = "line") DataTypes type
    ) {
        return chartType == Types.chart ? "chart_city.html" : "chart_stock_city.html";
    }
}