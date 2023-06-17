package com.ac.upt.sitemapnewsmanager.payloads.requests;

import com.ac.upt.sitemapnewsmanager.models.Role;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {
  private String username;

  @Size(max = 50)
  @Email
  private String email;

  private Role roles;

  private Long id;
}
