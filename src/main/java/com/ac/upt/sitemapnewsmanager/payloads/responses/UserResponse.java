package com.ac.upt.sitemapnewsmanager.payloads.responses;

import com.ac.upt.sitemapnewsmanager.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private Role role;
}
