package com.ac.upt.sitemapnewsmanager.payloads.requests;

import com.ac.upt.sitemapnewsmanager.models.Role;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 7, max = 40)
    private String password;

    private Role role;
}
