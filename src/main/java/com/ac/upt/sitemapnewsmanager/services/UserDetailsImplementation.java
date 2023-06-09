package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.models.Role;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class UserDetailsImplementation implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String email;

  private String username;

  @JsonIgnore private String password;

  private Role role;

  public static UserDetailsImplementation build(User user) {
    return new UserDetailsImplementation(
        user.getId(), user.getEmail(), user.getUsername(), user.getPassword(), user.getRole());
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImplementation user = (UserDetailsImplementation) o;
    return Objects.equals(id, user.id);
  }
}
