/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Chart Controller
 *
 * Endpoint for generating charts with measurement data in realtime.
 */
@Controller
@Api(value = "Chart REST Endpoint", tags = "chart")
public class ChartController {

    private enum Types { chart, stock }
    private enum DataTypes { line, spline, area, areaspline, bar, column }

    /**
     * Forwards the user to html endpoints, which can process the passed parameters
     *
     * @param chipId Chip-Id of of the requested sensor
     * @param from Begin of the requested time range (unix timestamp in milliseconds)
     * @param to End of the requested time range (unix timestamp in milliseconds)
     * @param fieldIndex Index of the requested data field (e.g. 0 for PM10, 1 for PM2.5, ...)
     * @param width Width of the generated chart
     * @param height Height of the generated chart
     * @param chartType Chart type (chart / stock)
     * @param type Line type (line / spline / area / areaspline / bar / column)
     * @return String for redirecting the user to the appropriate html resource
     */
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

    /**
     * Forwards the user to html endpoints, which can process the passed parameters
     *
     * @param country Name of a country which you want to aggregate over
     * @param from Begin of the requested time range (unix timestamp in milliseconds)
     * @param to End of the requested time range (unix timestamp in milliseconds)
     * @param fieldIndex Index of the requested data field (e.g. 0 for PM10, 1 for PM2.5, ...)
     * @param width Width of the generated chart
     * @param height Height of the generated chart
     * @param chartType Chart type (chart / stock)
     * @param type Line type (line / spline / area / areaspline / bar / column)
     * @return String for redirecting the user to the appropriate html resource
     */
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

    /**
     * Forwards the user to html endpoints, which can process the passed parameters
     *
     * @param country Name of a country where the requested city is located in
     * @param city Name of a city which you want to aggregate over
     * @param from Begin of the requested time range (unix timestamp in milliseconds)
     * @param to End of the requested time range (unix timestamp in milliseconds)
     * @param fieldIndex Index of the requested data field (e.g. 0 for PM10, 1 for PM2.5, ...)
     * @param width Width of the generated chart
     * @param height Height of the generated chart
     * @param chartType Chart type (chart / stock)
     * @param type Line type (line / spline / area / areaspline / bar / column)
     * @return String for redirecting the user to the appropriate html resource
     */
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