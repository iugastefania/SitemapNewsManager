package com.ac.upt.sitemapnewsmanager.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/testjenkins")
    public String test(){
        return "TEST";
    }

    @GetMapping("/test2")
    public String test2(){
        return "TEST2";
    }
}
