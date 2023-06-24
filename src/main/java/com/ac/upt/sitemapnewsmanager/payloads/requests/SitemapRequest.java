package com.ac.upt.sitemapnewsmanager.payloads.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SitemapRequest {

  private String loc;
  private String channel;

}
