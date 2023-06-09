package com.dxvalley.nedajpaymnetbackend.security.demoController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Well done Motuma, Secuirity authentication");
    }
}
