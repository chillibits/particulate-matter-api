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
    public String index(
            @RequestParam(defaultValue = "3953497") long chipId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex
    ) {
        return "index.html";
    }
}
