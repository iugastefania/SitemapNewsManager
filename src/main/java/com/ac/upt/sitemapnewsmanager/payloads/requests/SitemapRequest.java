package com.ac.upt.sitemapnewsmanager.payloads.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SitemapRequest {

  private String loc;
  private String channel;


  public SitemapRequest(String loc, String channel) {
    this.loc = loc;
    this.channel=channel;
  }
}
