package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "sitemaps", schema = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sitemap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  private String loc;

  private String channel;

  public Sitemap(String loc, String channel) {
    this.loc = loc;
    this.channel = channel;
  }
}
