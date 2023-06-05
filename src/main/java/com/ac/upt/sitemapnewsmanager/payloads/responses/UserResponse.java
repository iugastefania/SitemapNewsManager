package com.ac.upt.sitemapnewsmanager.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;

//    public UserResponse(Long id, String username, String email, String role) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.role = role;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getRoles() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
}
