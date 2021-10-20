package com.tournabay.api.controller;

import com.tournabay.api.payload.License;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class LicenseController {

    @GetMapping("/licencja/sort")
    public ResponseEntity<?> license() {
        License license = new License();
        license.setLdt(LocalDateTime.of(2023, 1, 1, 0, 0));
        return ResponseEntity.ok(license);
    }
}
