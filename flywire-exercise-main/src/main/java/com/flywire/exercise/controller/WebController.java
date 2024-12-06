package com.flywire.exercise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/employees")
    public String viewEmployees() {
        return "employees";
    }
}
