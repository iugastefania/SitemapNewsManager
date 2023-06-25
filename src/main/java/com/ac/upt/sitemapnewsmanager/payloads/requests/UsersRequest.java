package com.ac.upt.sitemapnewsmanager.payloads.requests;

import com.ac.upt.sitemapnewsmanager.models.Role;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersRequest {
  @NotBlank private Role role;
}
