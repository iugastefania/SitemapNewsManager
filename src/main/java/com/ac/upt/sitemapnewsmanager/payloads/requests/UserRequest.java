package com.ac.upt.sitemapnewsmanager.payloads.requests;

import com.ac.upt.sitemapnewsmanager.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;

    @Size(max = 50)
    @Email
    private String email;

    private Role roles;

    private Long id;

}
