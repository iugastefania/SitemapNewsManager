package com.ac.upt.sitemapnewsmanager.services;


import com.ac.upt.sitemapnewsmanager.models.Role;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class UserDetail implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String email;

    private String username;

    @JsonIgnore
    private String password;

    private Role role;

    public static UserDetail build(User user) {
        return new UserDetail(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetail user = (UserDetail) o;
        return Objects.equals(id, user.id);
    }

//    public String getRole() {
//        return role;
//    }
//    public Long getId() {
//        return id;
//    }
//    public String getEmail() {
//        return email;
//    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singletonList(new SimpleGrantedAuthority(role));
//    }
//    @Override
//    public String getPassword() {
//        return password;
//    }
//    @Override
//    public String getUsername() {
//        return username;
//    }
//    public UserDetail(Long id, String email, String username, String password,
//                           String role) {
//        this.id = id;
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.role = role;
//    }

}
