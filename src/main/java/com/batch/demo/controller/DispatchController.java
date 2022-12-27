package com.batch.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DispatchController {
    
    @GetMapping("/login")
    public String login() {
        return "login.html";
    }
}
