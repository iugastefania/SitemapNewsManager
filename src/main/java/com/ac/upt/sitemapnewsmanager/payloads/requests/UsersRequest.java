package com.ac.upt.sitemapnewsmanager.payloads.requests;

import com.ac.upt.sitemapnewsmanager.models.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UsersRequest {
    @NotBlank
    private Role role;
}