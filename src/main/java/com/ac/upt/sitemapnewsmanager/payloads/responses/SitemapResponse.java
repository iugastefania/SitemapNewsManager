package com.ac.upt.sitemapnewsmanager.payloads.responses;

import com.ac.upt.sitemapnewsmanager.models.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
 @AllArgsConstructor
@EqualsAndHashCode
public class SitemapResponse {
  private Long id;

  private String loc;

  private String channel;

}
