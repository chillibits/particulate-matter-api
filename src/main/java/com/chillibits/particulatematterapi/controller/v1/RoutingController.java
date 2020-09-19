/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Api(value = "Routing REST Endpoint", tags = "routing", hidden = true)
public class RoutingController {

    @Autowired
    private UserService userService;

    /**
     * Redirects to the API documentation page
     *
     * @return Redirect string
     */
    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ApiOperation(value = "Redirects the user to the swagger ui page", hidden = true)
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }

    /**
     * Confirms an user account
     *
     * @param token Randomly generated confirmation token, which is included in confirmation links
     * @return Redirect to the pmapp website to show a success / failure message
     */
    @RequestMapping(method = RequestMethod.GET, path = "/confirm")
    @ApiOperation(value = "Confirms an user account", hidden = true)
    public String confirmAccount(@RequestParam String token) {
        return "redirect:https://www.chillibits.com/pmapp?p=confirmation/" + (userService.confirmAccount(token) ? "success" : "failure");
    }
}