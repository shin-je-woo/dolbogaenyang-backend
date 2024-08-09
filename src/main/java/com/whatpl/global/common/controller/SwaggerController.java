package com.whatpl.global.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/docs/api")
    public String index() {
        return "/docs/index.html";
    }
}