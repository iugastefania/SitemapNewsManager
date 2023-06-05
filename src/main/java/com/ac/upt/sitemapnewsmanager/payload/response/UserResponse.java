package com.ac.upt.sitemapnewsmanager.payload.response;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    private Long points;

    public UserResponse(Long id, String username, String email, String role, Long points) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.points=points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return role;
    }
}
