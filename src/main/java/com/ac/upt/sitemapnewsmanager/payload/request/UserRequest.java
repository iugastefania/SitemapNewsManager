package com.ac.upt.sitemapnewsmanager.payload.request;

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

    private String roles;

    private Integer points;
    private Long id;

}
