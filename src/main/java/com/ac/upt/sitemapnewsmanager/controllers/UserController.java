package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Role;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.ac.upt.sitemapnewsmanager.payloads.requests.AuthenticationRequest;
import com.ac.upt.sitemapnewsmanager.payloads.requests.RegisterRequest;
import com.ac.upt.sitemapnewsmanager.payloads.requests.UsersRequest;
import com.ac.upt.sitemapnewsmanager.payloads.responses.MessageResponse;
import com.ac.upt.sitemapnewsmanager.payloads.responses.UserResponse;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import com.ac.upt.sitemapnewsmanager.security.JsonWebToken.CustomJwtUtils;
import com.ac.upt.sitemapnewsmanager.services.UserDetailsImplementation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {
  @Autowired UserRepository userRepository;

  @Autowired PasswordEncoder encoder;

  @Autowired AuthenticationManager authenticationManager;

  @Autowired CustomJwtUtils customJwtUtils;

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> registerUser(
      @Valid @RequestBody RegisterRequest registerRequest,
      @AuthenticationPrincipal UserDetailsImplementation userDetails) {
    log.info("Register user with username: " + registerRequest.getUsername());

//    if ((registerRequest.getRole() == Role.ADMINISTRATOR
//            || registerRequest.getRole() == Role.EDITOR)
//        && (userDetails == null || !userDetails.getRole().equals(Role.ADMINISTRATOR))) {
//      return ResponseEntity.badRequest()
//          .body(new MessageResponse("Only ADMINISTRATOR can create an ADMINISTRATOR user."));
//    }

    if (Boolean.TRUE.equals(userRepository.existsByEmail(registerRequest.getEmail()))) {
      return ResponseEntity.badRequest().body(new MessageResponse("Email already used!"));
    }

    if (Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.getUsername()))) {
      return ResponseEntity.badRequest().body(new MessageResponse("Username already exists!"));
    }

    User user =
        new User(
            registerRequest.getEmail(),
            registerRequest.getUsername(),
            encoder.encode(registerRequest.getPassword()),
            registerRequest.getRole());

    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Registered successfully!"));
  }

  @PostMapping("/authentication")
  public ResponseEntity<UserResponse> authenticateUser(
      @Valid @RequestBody AuthenticationRequest authenticationRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImplementation userDetails =
        (UserDetailsImplementation) authentication.getPrincipal();
    ResponseCookie jwtCookie = customJwtUtils.generateJwtCookie(userDetails);

    log.info("Authentication with username: " + authenticationRequest.getUsername());

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(
            new UserResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getUsername(),
                userDetails.getRole()));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    log.info("Logout user");
    ResponseCookie cookie = customJwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("Signed out!"));
  }

  @GetMapping("/getAllUsers")
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<User> users = userRepository.findAll();
    List<UserResponse> userResponses =
        users.stream()
            .map(
                user ->
                    new UserResponse(
                        user.getId(), user.getEmail(), user.getUsername(), user.getRole()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(userResponses);
  }

  @DeleteMapping("/deleteUser/{username}")
  public ResponseEntity<MessageResponse> deleteUser(
      @PathVariable String username,
      @AuthenticationPrincipal UserDetailsImplementation userDetails) {
    if (userDetails == null || !userDetails.getRole().equals(Role.ADMINISTRATOR)) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Only an ADMINISTRATOR can delete a user."));
    }
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      userRepository.delete(user);
      return ResponseEntity.ok(new MessageResponse("User deleted successfully."));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/changeUserRole/{username}/role")
  public ResponseEntity<MessageResponse> changeUserRole(
      @PathVariable String username,
      @RequestBody UsersRequest usersRequest,
      @AuthenticationPrincipal UserDetailsImplementation userDetails) {
    if (userDetails == null || !userDetails.getRole().equals(Role.ADMINISTRATOR)) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Only an ADMINISTRATOR can change user roles."));
    }

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setRole(usersRequest.getRole());
      userRepository.save(user);
      return ResponseEntity.ok(new MessageResponse("User role changed successfully."));
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
