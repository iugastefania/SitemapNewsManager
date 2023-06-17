package com.ac.upt.sitemapnewsmanager.payloads.responses;

import com.ac.upt.sitemapnewsmanager.models.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArticleResponse {
  private Long id;

  private String loc;

  private String lastmod;

  private String channelName;

  private String title;

  private String description;

  private String thumbnail;

  private User user;
}
