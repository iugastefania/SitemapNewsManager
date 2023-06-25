package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(
    name = "users",
    schema = "news",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email")
    })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  @Email
  private String email;

  @Column(unique = true, nullable = false, length = 20)
  private String username;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<Article> articles = new ArrayList<>();

  public User(String email, String username, String password, Role role) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.role = role;
  }
}
