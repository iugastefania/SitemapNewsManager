package com.ac.upt.sitemapnewsmanager.payloads.requests;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
  @NotBlank private String username;

  @NotBlank private String password;
}
