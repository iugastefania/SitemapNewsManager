package com.ac.upt.sitemapnewsmanager.payloads.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleRequest {
  private String loc;
  private String lastmod;
  private String channelName;
  private String title;
  private String description;
  private String thumbnail;
  private String user;
  private String sitemapLoc;
}
