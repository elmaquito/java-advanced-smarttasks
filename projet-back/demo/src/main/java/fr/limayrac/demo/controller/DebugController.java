package fr.limayrac.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return Map.of(
                "subject", jwtToken.getToken().getSubject(),
                "claims", jwtToken.getToken().getClaims()
            );
        }
        return Map.of("message", "Not authenticated with JWT");
    }
}
