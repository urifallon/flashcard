package com.flashcard.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/register")
    public String ShowRegisterPage(){
        return "register";
    }

    @GetMapping("/login")
    public String ShowLoginPage(){
        return "login";
    }
}
