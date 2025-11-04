package br.com.esg.energia.controller;

import br.com.esg.energia.security.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    record TokenRequest(@NotBlank String username, @NotBlank String roles) {}

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest req) {
        String[] roles = req.roles().split(",");
        String jwt = jwtUtil.generateToken(req.username(), roles);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}


