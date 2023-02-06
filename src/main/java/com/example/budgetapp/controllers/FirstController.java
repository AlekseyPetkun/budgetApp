package com.example.budgetapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Запуск первого веб-приложения.
 */

@RestController
public class FirstController {

    @GetMapping
    public String helloWeb() {
        return "hello web!";
    }

    @GetMapping("/path/to/page")
    public String page(@RequestParam String page) {
        return "page - " + page;
    }
}
