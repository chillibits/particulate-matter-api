/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RoutingConfig {
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String swagger() {
        return "redirect:/swagger-ui.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chart")
    public String index() {
        return "index.html";
    }
}
