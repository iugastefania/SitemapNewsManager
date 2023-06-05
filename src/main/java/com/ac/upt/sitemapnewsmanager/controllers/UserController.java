package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.User;
import com.ac.upt.sitemapnewsmanager.payloads.requests.AuthenticationRequest;
import com.ac.upt.sitemapnewsmanager.payloads.requests.RegisterRequest;
import com.ac.upt.sitemapnewsmanager.payloads.responses.MessageResponse;
import com.ac.upt.sitemapnewsmanager.payloads.responses.UserResponse;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import com.ac.upt.sitemapnewsmanager.security.JsonWebToken.JwtUtils;
import com.ac.upt.sitemapnewsmanager.services.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register user with username: " + registerRequest.getUsername());

        if (Boolean.TRUE.equals(userRepository.existsByEmail(registerRequest.getEmail()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already used!"));
        }
        if (Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.getUsername()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists!"));
        }

        User user = new User(registerRequest.getEmail(),
                registerRequest.getUsername(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getRole());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Registered successfully!"));
    }

    @PostMapping("/authentication")
    public ResponseEntity<UserResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetails = (UserDetail) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserResponse(userDetails.getId(),
                        userDetails.getEmail(),
                        userDetails.getUsername(),
                        userDetails.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        log.info("Logout user");
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Signed out!"));
    }
}
