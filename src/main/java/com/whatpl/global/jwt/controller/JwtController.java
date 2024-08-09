package com.whatpl.global.jwt.controller;

import com.whatpl.global.jwt.dto.JwtResponse;
import com.whatpl.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<JwtResponse> reIssue(@RequestParam String refreshToken) {
        JwtResponse jwtResponse = jwtService.reIssueToken(refreshToken);
        return ResponseEntity.ok(jwtResponse);
    }
}
