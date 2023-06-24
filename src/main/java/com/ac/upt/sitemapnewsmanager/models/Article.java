package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "articles", schema = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(name = "sitemapId")
  private Long sitemapId;

  private String loc;
  private String lastmod;
  private String channelName;

  @Column(columnDefinition = "text")
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "text")
  private String thumbnail;

  @Column(name = "userId")
  private Long userId;

  public Article(
          Long sitemapId,
          String loc,
          String lastmod,
          String channelName,
          String title,
          String description,
          String thumbnail,
          Long userId) {
    this.sitemapId = sitemapId;
    this.loc = loc;
    this.lastmod = lastmod;
    this.channelName = channelName;
    this.title = title;
    this.description = description;
    this.thumbnail = thumbnail;
    this.userId = userId;
  }
}
