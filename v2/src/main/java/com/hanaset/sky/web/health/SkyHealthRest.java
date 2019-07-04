package com.hanaset.sky.web.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkyHealthRest {

    @GetMapping("/health")
    public ResponseEntity health() { return ResponseEntity.ok("Ok");}

    @GetMapping("")
    public ResponseEntity firstPage() { return ResponseEntity.ok("Ok");}
}
